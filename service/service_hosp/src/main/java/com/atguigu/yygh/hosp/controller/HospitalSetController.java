package com.atguigu.yygh.hosp.controller;

import com.atguigu.yygh.common.utils.MD5;
import com.atguigu.yygh.hosp.service.HospitalSetService;
import com.atguigu.yygh.model.hosp.HospitalSet;
import com.atguigu.yygh.vo.hosp.HospitalSetQueryVo;
import com.atuguigu.yygh.common.result.Result;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Api(tags = "医院设置管理")   // 提示
@RestController
@RequestMapping("/admin/hosp/hospitalSet")
public class HospitalSetController {
    @Autowired
    private HospitalSetService hospitalSetService;

    //1、 查询医院设置表里的所有信息
    @ApiOperation(value = "获取所有医院设置信息")
    @GetMapping("/findAll")
    public Result findAllHospitalSet(){
        // 调用service的方法
        List<HospitalSet> list = hospitalSetService.list();
        return Result.ok(list);
    }

    //2、 逻辑删除医院设置
    @ApiOperation(value = "逻辑删除医院设置信息")
    @DeleteMapping("/{id}")
    public Result removeHospSet(@PathVariable("id") Long id){
        boolean b = hospitalSetService.removeById(id);
        if(b){
            return Result.ok();
        }else {
            return Result.fail();
        }
    }

    // 3、条件分页查询
    @ApiOperation(value = "条件分页查询医院设置信息")
    @PostMapping("/findPageHospital/{current}/{limit}")
    public Result findPageHospital(@PathVariable("current") long current,
                                   @PathVariable("limit") long limit,
                                   @RequestBody(required = false) HospitalSetQueryVo hospitalSetQueryVo){

        // 【注】@RequestBody表示传入的数据分装进入对象 required表示可以为空  结合@PostMapping注解

        // 创建page对象，传递当前页，每页记录数
        Page<HospitalSet> page = new Page<>(current,limit);
        // 构建条件
        QueryWrapper<HospitalSet> wrapper = new QueryWrapper<>();
        String hosname = hospitalSetQueryVo.getHosname();   // 医院的名称
        String hoscode = hospitalSetQueryVo.getHoscode();   // 医院的编号
        if(!StringUtils.isEmpty(hosname)){
            wrapper.like("hosname",hospitalSetQueryVo.getHosname());
        }
        if(!StringUtils.isEmpty(hoscode)){
            wrapper.eq("hoscode",hospitalSetQueryVo.getHoscode());
        }

        // 调用方法实现分页查询
        Page<HospitalSet> pageHospitalSet = hospitalSetService.page(page, wrapper);

        return Result.ok(pageHospitalSet);
    }


    // 4、添加医院设置
    @ApiOperation(value = "添加医院设置信息")
    @PostMapping("saveHospitalSet")
    public Result saveHospitalSet(@RequestBody HospitalSet hospitalSet){
        // 设置状态 1 能使用 0 不能使用
        hospitalSet.setStatus(1);
        Date date = new Date();
        hospitalSet.setCreateTime(date);
        // 签名密钥
        Random random = new Random();
        hospitalSet.setSignKey( MD5.encrypt(System.currentTimeMillis() + "" + random.nextInt(1000)));

        // 调用server方法
        boolean save = hospitalSetService.save(hospitalSet);
        if(save){
            return Result.ok();
        }else {
            return Result.fail();
        }

    }

    // 5、根据id获取医院设置接口
    @ApiOperation(value = "根据id获取医院信息")
    @GetMapping("/getHospSet/{id}")
    public Result getHospSet(@PathVariable("id") Long id){
        /*int a = 1 / 0;*/
        HospitalSet byId = hospitalSetService.getById(id);
        return Result.ok(byId);
    }

    // 6、修改医院设置
    @ApiOperation(value = "修改医院设置信息")
    @PostMapping("/updateHospitalSet")
    public Result updateHospitalSet(@RequestBody HospitalSet hospitalSet){
        boolean b = hospitalSetService.updateById(hospitalSet);
        if(b){
            return Result.ok();
        }else {
            return Result.fail();
        }
    }

    // 7、批量删除医院设置
    @ApiOperation(value = "批量删除医院设置信息")
    @DeleteMapping("/batchRemove")
    public Result batchRemoveHospitalSet(@RequestBody List<Long> list){
        hospitalSetService.removeByIds(list);
        return Result.ok();
    }

    //8、医院设置锁定和解锁
    @ApiOperation(value = "锁定与解锁")
    @PutMapping("/lockHospitalSet/{id}/{status}")
    public Result lockHospitalSet(@PathVariable("id") Long id,
                                  @PathVariable("status") Integer status){

        // 根据id查询医院的设置信息
        HospitalSet byId = hospitalSetService.getById(id);
        // 设置医院的状态
        byId.setStatus(status);
        // 调用方法
        hospitalSetService.updateById(byId);
        return Result.ok();

    }

    //9、发送签名的密钥
    @ApiOperation(value = "发送密钥")
    @PutMapping("/sendKey/{id}")
    public Result sendKeyHospitalSet(@PathVariable("id") Long id){
        HospitalSet byId = hospitalSetService.getById(id);
        String signKey = byId.getSignKey();
        String hoscode = byId.getHoscode();
        // TODO 发送短信
        return Result.ok();
    }
}
