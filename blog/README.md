# Blog example for XJOIN

To index:

```sh
cd src/python
python index.py http://localhost:8983/solr/products/update?commit=true ../../GoogleProducts.csv
```

To run the web api:

```sh
cd src/python
python offer.py ../../GoogleProducts.csv
```
