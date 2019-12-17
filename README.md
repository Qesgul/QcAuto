# QcAuto
qc系统的二次开发


记录一下大致遇到的坑:
    jira-rest-java-client包的版本差异明显：2.0.0-m2和0.5-m6包许多方法都存在一定差异，此项目通过2.0.0-m2开发
    
    由于maven配置原因，无法直接下载到对应架包，需要在pom.xml中增加
    <repositories>
        <repository>
            <id>atlassian-public</id>
            <url>https://m2proxy.atlassian.com/repository/public</url>
            <snapshots>
                <enabled>true</enabled>
                <updatePolicy>daily</updatePolicy>
                <checksumPolicy>warn</checksumPolicy>
            </snapshots>
            <releases>
                <enabled>true</enabled>
                <checksumPolicy>warn</checksumPolicy>
            </releases>
        </repository>
    </repositories>

    <pluginRepositories>
        <pluginRepository>
            <id>atlassian-public</id>
            <url>https://m2proxy.atlassian.com/repository/public</url>
            <releases>
                <enabled>true</enabled>
                <checksumPolicy>warn</checksumPolicy>
            </releases>
            <snapshots>
                <checksumPolicy>warn</checksumPolicy>
            </snapshots>
        </pluginRepository>
    </pluginRepositories>
    如下来使架包可以正常下载使用
    
    许多公司的jira基本经过了二次包装，官方api提供的接口只有参考意义，并无实用价值，需要管理员账户才解决属于自己公司的一些配置，特别是key和id一些字段
  
  自动化提交的代码并无难处，只是提交的qc大部分字段均需要自定义，官方并未暴露详细的方法，只能通过map提交，后续需要慢慢来补此坑，
  
  URI的使用问题暂时未解决，只要该api使用了URI，会导致程序无法正常结束，目前尚未知道原因
  
  上传文件的方法官方提供的无返回值，导致有可能出现文件还未提交，程序执行结束的坑
