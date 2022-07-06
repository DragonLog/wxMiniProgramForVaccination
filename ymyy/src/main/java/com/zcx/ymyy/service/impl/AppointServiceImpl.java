package com.zcx.ymyy.service.impl;

import com.zcx.ymyy.entity.Appoint;
import com.zcx.ymyy.entity.Pay;
import com.zcx.ymyy.entity.Plan;
import com.zcx.ymyy.mapper.AppointMapper;
import com.zcx.ymyy.service.AppointService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcx.ymyy.service.PayService;
import com.zcx.ymyy.service.PlanService;
import com.zcx.ymyy.service.VaccineService;
import com.zcx.ymyy.util.QRCodeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.UUID;


@Service
public class AppointServiceImpl extends ServiceImpl<AppointMapper, Appoint> implements AppointService {

    @Autowired
    private PlanService planService;

    @Value("${upload.image.qrCodeImage.url}")
    private String uploadImageQrCodeImageUrl;

    @Autowired
    private PayService payService;

    @Autowired
    private VaccineService vaccineService;

    @Autowired
    private AppointService appointService;

    @Override
    @Transactional(rollbackFor = Exception.class)   //出错回滚
    public void appoint(Appoint appoint, Plan plan) throws Exception {  //预约
        try {
            uploadImageQrCodeImageUrl = new String(uploadImageQrCodeImageUrl.getBytes("iso8859-1"), "UTF-8");   //application.properties有中文需要转码
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("字符转码异常！");
        }

        if (appoint == null || plan == null) {
            throw new RuntimeException("参数有误");
        }

        plan.setAmount(plan.getAmount() - 1);
        planService.updateById(plan);
        appoint.setStatus(0);
        appoint.setCreateTime(new Date());
        save(appoint);
        String fileName = UUID.randomUUID().toString().replace("-", "") + ".jpg";
        String path = QRCodeUtils.generateQRCode(appoint.getId() + "", 400, 400, "jpg", uploadImageQrCodeImageUrl + "/" + fileName);
        appoint.setQrCodeUrl("/" + fileName);
        updateById(appoint);

        Pay pay = new Pay();
        pay.setCost(vaccineService.getById(plan.getVaccineId()).getPrice());
        pay.setCreateTime(new Date());
        pay.setAppointId(appoint.getId());
        payService.save(pay);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelAppoint(Appoint appoint, Plan plan) { //取消预约
        if (appoint == null || plan == null) {
            throw new RuntimeException("参数有误");
        }

        appoint.setStatus(6);
        appointService.updateById(appoint);

        plan.setAmount(plan.getAmount() + 1);
        planService.updateById(plan);
    }

}
