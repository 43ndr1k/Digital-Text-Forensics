# __filename__: dblptest.py
#
# __description__: "Spider" for dblp
#
# __remark__:
#
# __todos__:
#
# Created by Tobias Wenzel in November 2017
# Copyright (c) 2017 Tobias Wenzel

import requests
import json
from datetime import datetime
import pickle
from pathlib import Path
from lxml import etree


class ArticleMetaDataSearcher:
    REQUEST_URL = "http://dblp.uni-trier.de/search/publ?q="
    head = {
        "Host": "dblp.uni-trier.de",
        "User-Agent": "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:56.0) Gecko/20100101 Firefox/56.0",
        "Accept": "text/javascript, application/javascript, application/ecmascript, application/x-ecmascript, */*; q=0.01",
        "Accept-Language": "de,en-US;q=0.7,en;q=0.3",
        "Accept-Encoding": "gzip, deflate",
        "DNT": "1",
        "Connection": "keep-alive"
    }

    def __init__(self, use_n_first=3):
        """

        :param use_n_first:
        """
        self.use_n_first = use_n_first
        self.iteration = 0

    def get_article_obj(self, tile_like=""):
        """
        :param tile_like:
        :return:
        """
        if tile_like:
            req = self.REQUEST_URL + tile_like
            try:
                response = requests.get(req, self.head)
            except requests.exceptions.ConnectionError as e:
                print(e)
            tree = etree.HTML(response.text)


            try:
                articles = tree.xpath('//ul[contains(@class,"publ-list")]/li[starts-with(@class,"entry")]/nav/ul/li')
            except IndexError:
                return

            article_obj_array = []
            i = 0
            for article in articles:

                article_obj = {}
                try:
                    article_xml_link = article.xpath('div[contains(@class,"body")]/ul/li/a/@href')[-1]
                except IndexError:
                    continue
                if article_xml_link.endswith(".xml"):
                    if i == self.use_n_first:
                        break
                    i += 1
                    try:
                        response = requests.get(article_xml_link, self.head)
                    except requests.exceptions.ConnectionError as e:
                        print(e)

                    xml_file = etree.XML(response.content)
                    article_obj['author'] = ", ".join(xml_file.xpath('//author/text()'))
                    article_obj['title'] = xml_file.xpath('//title/text()')
                    article_obj['year'] = xml_file.xpath('//year/text()')
                    article_obj_array.append(article_obj)

            if article_obj_array:
                self.iteration = 0
                return article_obj_array
            elif self.iteration != 1:
                self.iteration += 1
                # could cut more and more. @todo
                title_like = " ".join(tile_like.split(" ")[:self.use_n_first])
                print("could not find original title. search for %s"%title_like)

                return self.get_article_obj(tile_like=title_like)




if __name__ == '__main__':
    """
    @todo StyleBased
    @todo walk through xml for testing. 
    -> better in java
    """
    searcher = ArticleMetaDataSearcher(use_n_first=1)
    title_like = "Comparing Frequency and StyleBased Features for Twitter Author Identification"
    article_objs = searcher.get_article_obj(tile_like=title_like)

    for obj in article_objs:
        print(obj)