package com.lin.sleeve.mockdata;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lin.sleeve.model.Banner;
import com.lin.sleeve.model.BannerItem;
import com.lin.sleeve.model.GridCategory;
import com.lin.sleeve.model.Sku;
import com.lin.sleeve.model.Spu;
import com.lin.sleeve.model.SpuImg;
import com.lin.sleeve.model.Theme;
import com.lin.sleeve.repository.BannerItemRepository;
import com.lin.sleeve.repository.BannerRepository;
import com.lin.sleeve.repository.CategoryRepository;
import com.lin.sleeve.repository.GridCategoryRepository;
import com.lin.sleeve.repository.SkuRepository;
import com.lin.sleeve.repository.SpuImageRepository;
import com.lin.sleeve.repository.SpuRepository;
import com.lin.sleeve.repository.ThemeRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2021/1/22 18:59
 */
@Component
public class MockDataSource {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private BannerRepository bannerRepository;

    @Autowired
    private BannerItemRepository bannerItemRepository;

    @Autowired
    private SpuRepository spuRepository;

    @Autowired
    private SpuImageRepository spuImageRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private SkuRepository skuRepository;

    @Autowired
    private GridCategoryRepository gridCategoryRepository;

    @Autowired
    private ThemeRepository themeRepository;

    public void insertData() {
        System.out.println("-------------------------start inserting Data-------------------------");
        //saveHomeBanner(); done
        //saveHomeGridCategory();done
        //saveDetails();done
        //saveCategory();done
        //saveHomeThemes();
        //saveHomeThemeSpu();
        System.out.println("-------------------------end inserting Data-------------------------");
    }

    private void saveGridCategory() {
        InputStream resource;
        try {
            resource = new ClassPathResource("json/home/category.json").getInputStream();
            List<GridCategory> categoryList = mapper.readValue(resource, new TypeReference<List<GridCategory>>() {
            });
            gridCategoryRepository.saveAll(categoryList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveCategory() {
        InputStream resource;
        try {
            resource = new ClassPathResource("json/category/categories.json").getInputStream();
            CategoriesAll categoriesAll = mapper.readValue(resource, CategoriesAll.class);
            categoryRepository.saveAll(categoriesAll.getRoots());
            categoryRepository.saveAll(categoriesAll.getSubs());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveDetails() {
        for (int i = 1; i <= 17; i++) {
            saveDetail(i);
        }
    }

    private void saveDetail(int index) {
        InputStream resource;
        try {
            resource = new ClassPathResource("json/detail/detail-" + index + ".json").getInputStream();
            Spu spu = mapper.readValue(resource, Spu.class);
            List<Sku> skuList = spu.getSkuList();
            List<SpuImg> spuImgList = spu.getSpuImgList();
            skuRepository.saveAll(skuList);
            spuImageRepository.saveAll(spuImgList);
            spu.setSpuDetailImgList(null);
            spuRepository.save(spu);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveHomeBanner() {
        InputStream resource = null;
        try {
            resource = new ClassPathResource("json/home/banner-2.json").getInputStream();
            Banner banner = mapper.readValue(resource, Banner.class);
            List<BannerItem> items = banner.getItems();
            bannerItemRepository.saveAll(items);
            bannerRepository.save(banner);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(resource);
        }
    }

    private void saveHomeThemeSpu() {
        InputStream resource = null;
        try {
            resource = new ClassPathResource("json/home/theme-with-spu.json").getInputStream();
            Theme theme = mapper.readValue(resource, Theme.class);
            themeRepository.save(theme);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            close(resource);
        }
    }

    private void saveHomeThemes() {
        InputStream resource = null;
        try {
            resource = new ClassPathResource("json/home/themes.json").getInputStream();
            List<Theme> themes = mapper.readValue(resource, new TypeReference<List<Theme>>() {
            });
            themeRepository.saveAll(themes);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            close(resource);
        }
    }

    private void close(Closeable resource) {
        if (resource != null) {
            try {
                resource.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
