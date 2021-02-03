package com.lin.sleeve.mockdata;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lin.sleeve.model.Activity;
import com.lin.sleeve.model.Banner;
import com.lin.sleeve.model.BannerItem;
import com.lin.sleeve.model.Category;
import com.lin.sleeve.model.Coupon;
import com.lin.sleeve.model.GridCategory;
import com.lin.sleeve.model.Sku;
import com.lin.sleeve.model.Spu;
import com.lin.sleeve.model.SpuImg;
import com.lin.sleeve.model.Theme;
import com.lin.sleeve.repository.ActivityRepository;
import com.lin.sleeve.repository.BannerItemRepository;
import com.lin.sleeve.repository.BannerRepository;
import com.lin.sleeve.repository.CategoryRepository;
import com.lin.sleeve.repository.CouponRepository;
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
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 模拟生成数据
 *
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

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private ActivityRepository activityRepository;

    public void insertData() {
        System.out.println("-------------------------start inserting Data-------------------------");

        //saveHomeBanner(); done
        //saveHomeGridCategory();done

        //saveDetails();done

        //saveCategories();done

        //saveHomeThemes();done
        //saveHomeThemeSpu();done

        //saveAllCoupon();done
        //saveCategoriesWithCoupon();;done

        //saveHomeActivityWithCoupon();done

        System.out.println("-------------------------end inserting Data-------------------------");
    }

    /**
     * 首页：所有的 Banner
     */
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

    /**
     * 首页所有的分类
     */
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

    /**
     * 商品详情页：所有的商品详情
     */
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

    /**
     * 分类页：所有的分类
     */
    private void saveCategories() {
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

    /**
     * 首页：所有的主题
     */
    private void saveHomeThemes() {
        InputStream resource = null;
        try {
            resource = new ClassPathResource("json/home/themes.json").getInputStream();
            List<Theme> themes = mapper.readValue(resource, new TypeReference<List<Theme>>() {
            });
            themeRepository.saveAll(themes);
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
        } finally {
            close(resource);
        }
    }

    /**
     * 优惠券：所有的优惠券
     */
    private void saveAllCoupon() {
        InputStream resource = null;
        try {
            resource = new ClassPathResource("json/coupon/coupons.json").getInputStream();
            List<Coupon> couponList = mapper.readValue(resource, new TypeReference<List<Coupon>>() {
            });
            couponRepository.saveAll(couponList);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(resource);
        }
    }

    /**
     * 优惠券：二级分类与优惠券的关系
     */
    private void saveCategoriesWithCoupon() {
        saveCategoriesWithCoupon(15L);
        saveCategoriesWithCoupon(32L);
        saveCategoriesWithCoupon(35L);
    }

    private void saveCategoriesWithCoupon(Long cid) {
        Optional<Category> category = categoryRepository.findById(cid);
        if (!category.isPresent()) {
            return;
        }

        InputStream resource = null;
        try {
            resource = new ClassPathResource("json/coupon/c" + cid + "_coupon.json").getInputStream();
            List<Coupon> couponList = mapper.readValue(resource, new TypeReference<List<Coupon>>() {
            });
            Category c = category.get();
            c.setCouponList(couponList);
            categoryRepository.save(c);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(resource);
        }
    }

    /**
     * 首页活动与优惠券之间的关系
     */
    private void saveHomeActivityWithCoupon() {
        /*保存时暂时将 activity-a2_with_coupon.json 中的 coupons 改名为 coupon_list。*/
        InputStream resource = null;
        try {
            resource = new ClassPathResource("json/home/activity-a2_with_coupon.json").getInputStream();
            Activity activity = mapper.readValue(resource, Activity.class);
            activity.setStartTime(new Date());
            Date endTime = new Date();
            endTime.setYear(endTime.getYear() + 2);
            activity.setEndTime(endTime);
            activity.setName("a2");
            activityRepository.save(activity);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
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
