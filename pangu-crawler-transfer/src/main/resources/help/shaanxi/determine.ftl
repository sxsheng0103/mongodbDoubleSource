[
<#list taxML.body.taxML.sfzrdxxGrid.sfzrdxxGridlb as info>
    {
		"zsxmdm":"",
        "zsxmmc":"${info.zszmmc}",
		"zspmdm":"",
		"zspmmc":"${info.zspmmc}",
        "rdyxqq":"${info.rdyxqq}",
		"rdyxqz":"${info.rdyxqz}",
        "slhdwse":"${info.slhdwse}",
        "zszmdm":"",
		"zszmmc":"${info.zszmmc}",
        "zsdlfsmc":"${info.zsfs}",
        "gdsbz":"${info.gdslx}",
        "zsl":"",
        "sbqxdm":"",
		"sbqxmc":"${info.sbqx}",
		"nsqxdm":"",
        "nsqxmc":"${info.nsqx}"

    }
	<#if info_has_next>,</#if>
</#list>
]