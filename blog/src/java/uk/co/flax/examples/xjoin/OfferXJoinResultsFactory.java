package uk.co.flax.examples.xjoin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;

import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.search.xjoin.XJoinResults;
import org.apache.solr.search.xjoin.XJoinResultsFactory;

public class OfferXJoinResultsFactory
implements XJoinResultsFactory {
  private String url;
  private String field;
  private String discountField;

  @Override
  @SuppressWarnings("rawtypes")
  public void init(NamedList args) {
    url = (String)args.get("url");
    field = (String)args.get("field");
    discountField = (String)args.get("discountField");
  }

  /**
   * Use 'offers' REST API to fetch current offer data.
   */
  @Override
  public XJoinResults getResults(SolrParams params)
  throws IOException {
    try (HttpConnection http = new HttpConnection(url)) {
      JsonArray offers = (JsonArray)http.getJson();
      return new OfferResults(offers);
    }
  }

  /**
   * Results of the external search - methods like getXXX() are used
   * to expose the property XXX in the SOLR results.
   */
  public class OfferResults implements XJoinResults {
    private JsonArray offers;

    public OfferResults(JsonArray offers) {
      this.offers = offers;
    }

    public int getCount() {
      return offers.size();
    }

    @Override
    public Iterable getJoinIds() {
      List ids = new ArrayList<>();
      for (JsonValue offer : offers) {
        ids.add(((JsonObject)offer).getString(field));
      }
      return ids;
    }

    @Override
    public Object getResult(String joinIdStr) {
      for (JsonValue offer : offers) {
        String id = ((JsonObject)offer).getString(field);
        if (id.equals(joinIdStr)) {
          return new Offer(offer);
        }
      }
      return null;
    }
  }

  /**
   * A discount offer - methods like getXXX() are used to expose
   * properties that can be joined with each Solr result via the join
   * id field.
   */
  public class Offer {
    private JsonValue offer;

    public Offer(JsonValue offer) {
      this.offer = offer;
    }

    public double getDiscount() {
      return ((JsonObject)offer).getInt(discountField) * 0.01d;
    }
  }
}
