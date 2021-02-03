package com.lin.sleeve.api.v1;

import com.lin.sleeve.bo.PagerCounter;
import com.lin.sleeve.exception.ExceptionCodes;
import com.lin.sleeve.exception.http.NotFoundException;
import com.lin.sleeve.model.Spu;
import com.lin.sleeve.service.SpuService;
import com.lin.sleeve.util.CommonUtil;
import com.lin.sleeve.vo.PagingDozer;
import com.lin.sleeve.vo.SpuSimplifyVO;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Positive;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2021/1/22 20:10
 */
@RestController
@RequestMapping("/spu")
@Validated
public class SpuController {

    @Autowired
    private SpuService spuService;

    @GetMapping("/id/{id}/detail")
    public Spu getDetail(@PathVariable("id") @Positive Long id) {
        Spu spu = spuService.getSpu(id);
        if (spu == null) {
            throw new NotFoundException(ExceptionCodes.C_30003);
        }
        return spu;
    }

    /*
    分页的两种模式：
            方式1： pageNumber + pageSize
            方式2： start + count【用于移动端】
     */
    @GetMapping("/latest")
    public PagingDozer<Spu, SpuSimplifyVO> getLatestSpuList(
            @RequestParam(name = "start", defaultValue = "0") Integer start,/*从第 start 条数据开始*/
            @RequestParam(name = "count", defaultValue = "10") Integer count
    ) {
        PagerCounter pagerCounter = CommonUtil.convertToPageParameter(start, count);
        Page<Spu> page = spuService.getLatestSpuList(pagerCounter.getPage(), pagerCounter.getCount());
        if (page == null) {
            throw new NotFoundException(ExceptionCodes.C_30003);
        }
        return new PagingDozer<>(page, SpuSimplifyVO.class);
    }

    @GetMapping("/id/category/{id}")
    public PagingDozer<Spu, SpuSimplifyVO> getSpuListByCategoryId(
            @PathVariable(name = "id") @Positive(message = "{id.positive}") Long id,
            @RequestParam(name = "is_root", defaultValue = "false") Boolean isRoot,
            @RequestParam(name = "start", defaultValue = "0") Integer start,
            @RequestParam(name = "count", defaultValue = "10") Integer count
    ) {
        PagerCounter pagerCounter = CommonUtil.convertToPageParameter(start, count);
        Page<Spu> page = spuService.getSpuListByCategoryId(id, isRoot, pagerCounter.getPage(), pagerCounter.getCount());
        if (page == null) {
            throw new NotFoundException(ExceptionCodes.C_30003);
        }
        return new PagingDozer<>(page, SpuSimplifyVO.class);
    }

    ///////////////////////////////////////////////////////////////////////////
    // 学习代码
    ///////////////////////////////////////////////////////////////////////////
    /*Model 转换为 VO 的一种方式，但是在应对列表查询时比较麻烦，且不支持深度拷贝，也对性能有影响，因此可以使用 dozermapper。*/
    @GetMapping("/id/{id}/simplify")
    public SpuSimplifyVO getSimplifyVO(@PathVariable("id") @Positive Long id) {
        Spu spu = spuService.getSpu(id);
        if (spu == null) {
            throw new NotFoundException(ExceptionCodes.C_30003);
        }
        SpuSimplifyVO target = new SpuSimplifyVO();
        BeanUtils.copyProperties(spu, target);
        return target;
    }

}
