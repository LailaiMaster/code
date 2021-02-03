# part04-category_design：15 商品分类的设计

1. 【树表示法】用树的思想可以应对无限极分级，即每个分类都有一个 parentId。【不推荐，因为查询效率不高】
2. 【路径表示法】path 字段类似 `http://locahost/node1/node2/node3/node4`。
