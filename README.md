> **写在前面的话：项目copy下来后，直接用idea导入应该是没有问题的（项目的文件夹和包结构要正确），根据你的需要修改pom中的mysql驱动和application.properties文件中的数据库信息和图片路径后就可以直接启动使用了。**
## 基于微信小程序的疫苗预约接种系统
***
> **对某疫苗预约系统重构后二次开发**

1. 系统管理员基本功能：
     (1)查看数据分析图；
     (2)接种点信息增删改查；
     (3)接种点医护人员信息增删改查；
     (4)预约计划信息增删改查；
     (5)接种者信息删改查；
     (6)疫苗信息增删改查；
     (7)接种者支付历史信息查询；
     (8)接种者预约历史信息查询；
     (9)接种者签到历史信息查询；
     (10)接种者预检历史信息查询；
     (11)接种者接种历史信息查询；
     (12)接种者留观历史信息查询；
     (13)账号密码修改；
     (14)登录登出。
2. 接种者基本功能：
     (1)查看疫苗信息列表；
     (2)查看该疫苗对应的接种点信息列表；
     (3)查看该疫苗对应接种点的预约计划信息列表；
     (4)查看该疫苗对应接种点的预约计划信息并提交预约申请表单；
     (5)模拟支付疫苗单价；
     (6)查看接种二维码及其状态信息；
     (7)取消预约；
     (8)查看支付历史信息；
     (9)查看预约历史信息；
     (10)查看预检历史信息；
     (11)查看接种历史信息；
     (12)注册登录登出；
     (13)个人信息修改。
 >**注：接种者需全部满足(1.预约日期在预约计划的日期范围之内；2.预约日期在以今天为基准的明天到预约计划结束日期之间；3.疫苗的可预约量要大于0；4.对应时间段疫苗的剩余量要大于0；5.没有未完成的预约任务。)五个条件后，才能支付疫苗单价完成疫苗预约。**
  3. 医护人员基本功能：
      (1)签到信息登记；
      (2)预检信息登记；
      (3)接种信息登记；
      (4)留观信息登记；
      (5)签到信息登记历史查询；
      (6)预检信息登记历史查询；
      (7)接种信息登记历史查询；
      (8)留观信息登记历史查询；
      (9)登录登出；
      (10)个人信息修改。
  >**注：接种者需要严格按照签到、预检、接种、留观四个步骤完成疫苗接种。签到成功后的接种流程不受时间影响。签到需满足（1.预约日期匹配；2.预约时间段匹配；3.预约接种点匹配）三个条件，才能签到成功。**
4. 系统启动后会自动运行SpringBoot定时任务，会在每天凌晨处理过期的预约任务和流程未正常结束的接种任务（0代表待签到、1代表待预检、2代表待接种、3代表留观中、4代表接种流程结束、5代表预约过期、6代表接种者取消预约、7代表接种过程异常）。
4. 后端使用SpringMVC拦截器+jwt+自定义注解实现身份验证和权限控制。用户每次登录成功后，后端会返回token交由前端缓存，前端对后端相关接口发起的每次请求都需要携带该token进行验证。    
5. 系统使用二维码来充当接种者预约成功后的接种凭证，接种者需要在指定时间到指定地点，将二维码提供给相关医护人员扫描后完成接种流程。        
6. 系统在Service层同时操作多表修改时使用了注解式事务保证数据一致性。
7. 本人主要从事Java后端开发，因此本系统的前端UI非常粗糙，只达到了“能用但不好用”的目的，很多参数的判断都直接交给了后端。
8. 由于时间仓促，很多SQL语句的拼接都存在问题，但不影响初期使用。
9. 微信小程序使用uniapp搭配uview-ui组件库进行开发，后台管理页面使用layui-admin进行开发。
***
![示例图片](https://github.com/DragonLog/wxMiniProgramForVaccination/blob/main/pictureForExample/show1.jpg)
![示例图片](https://github.com/DragonLog/wxMiniProgramForVaccination/blob/main/pictureForExample/show2.jpg)
![示例图片](https://github.com/DragonLog/wxMiniProgramForVaccination/blob/main/pictureForExample/show3.jpg)
![示例图片](https://github.com/DragonLog/wxMiniProgramForVaccination/blob/main/pictureForExample/show4.jpg)
![示例图片](https://github.com/DragonLog/wxMiniProgramForVaccination/blob/main/pictureForExample/show5.jpg)
![示例图片](https://github.com/DragonLog/wxMiniProgramForVaccination/blob/main/pictureForExample/show6.jpg)
![示例图片](https://github.com/DragonLog/wxMiniProgramForVaccination/blob/main/pictureForExample/show7.jpg)
![示例图片](https://github.com/DragonLog/wxMiniProgramForVaccination/blob/main/pictureForExample/show8.jpg)
![示例图片](https://github.com/DragonLog/wxMiniProgramForVaccination/blob/main/pictureForExample/show9.jpg)
![示例图片](https://github.com/DragonLog/wxMiniProgramForVaccination/blob/main/pictureForExample/show10.jpg)
