# PublicationLinking
</br>
## 代码用法说明
**主程序在main包下的processor.java文件中，step1()用于将英文单位名转换为汉字**
**（翻译结果已经保存在translateResult.txt中，故代码中将此步骤注释掉了），**
**step2()将对表1和表2中的实体进行匹配，**
**输出格式为：**`表1ID  表2论文号`
</br>
##配置文件说明
**mysqlurl:jdbc:mysql://127.0.0.1/meisi?useUnicode=true&characterEncoding=utf8-----链接数据库的url</br>**
**username:root--------------------------------------------------------------------------------数据库用户名</br>**
**password:root--------------------------------------------------------------------------------数据库密码</br>**
</br>
**T1:sheet1$------------------------------------------------------------------------------------表1名</br>**
**T1.id:ID----------------------------------------------------------------------------------------表1字段——ID</br>**
**T1.name:NAME-------------------------------------------------------------------------------表1字段——人员姓名</br>**
**T1.company:AFFILIATION--------------------------------------------------------------------表1字段——单位名</br>**
</br>
**T2:sheet3$------------------------------------------------------------------------------------表2名</br>**
**T2.id:UT---------------------------------------------------------------------------------------表2字段——论文ID</br>**
**T2.nameCompany:C1------------------------------------------------------------------------表2字段——作者及单位</br>**
</br>
**thread:5---------------------------------------------------------------------------------------线程数</br>**
**cacheFile:translateResult.txt----------------------------------------------------------------存储翻译结果的文件</br>**
**resultFile:result.txt---------------------------------------------------------------------------存储匹配结果的文件</br>**
**threadshold:0.75------------------------------------------------------------------------------LCS相似度阈值</br>**
