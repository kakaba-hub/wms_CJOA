package com.cjoa.wms.dao;

import com.cjoa.wms.dto.ProductDto;
import com.cjoa.wms.dto.ProductOptionDto;

import java.util.List;
import java.util.Map;

public interface ProductMapper {

    List<ProductDto> selectAllProduct();

    List<ProductDto> selectAllProductForAdmin();

    List<ProductDto> selectProductByCategory(int code);

    List<ProductDto> selectProductByProdKeyword(String key);

    ProductDto selectProductOptionListByProdCode(int code);

    int addProduct(ProductDto productDto);

    int addProductOption(Map<String, List<ProductOptionDto>> map);

    int updateProduct(ProductDto productDto);

    int updateProductOption(ProductOptionDto productOptionDto);

    int deleteProduct(int code);
}
