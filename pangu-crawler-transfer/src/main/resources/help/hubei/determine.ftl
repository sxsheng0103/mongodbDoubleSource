[
<#list data as info>
    {
		"zsxmdm":"",
        "zsxmmc":"${info.ZSXMMC}",
		"zspmdm":"",
		"zspmmc":"${info.ZSPMMC}",
        "rdyxqq":"${info.RDYXQQ}",
		"rdyxqz":"${info.RDYXQZ}",
        "slhdwse":"${info.SLHDWSE}",
        "zszmdm":"",
		"zszmmc":"",
        "zsdlfsmc":"${info.ZSDLFSMC}",
        "gdsbz":"",
        "zsl":"${info.ZSL}",
        "sbqxdm":"",
		"sbqxmc":"${info.SBQXMC}",
		"nsqxdm":"",
        "nsqxmc":""

    }
	<#if info_has_next>,</#if>
</#list>
]