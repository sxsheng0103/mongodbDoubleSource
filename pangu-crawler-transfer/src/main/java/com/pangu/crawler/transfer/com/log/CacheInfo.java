package com.pangu.crawler.transfer.com.log;

public class CacheInfo {
        public static ThreadLocal<CInfo> threadglobalName = new ThreadLocal<CInfo>();
        public static void set(CInfo cinfo){
            threadglobalName.set(cinfo);
        }

        public  CacheInfo(String taskname,String taskid,String nsrsbh,String sz){
            CInfo cinfo = new CInfo();
            cinfo.setTaskname(taskname);
            cinfo.setNsrsbh(nsrsbh);
            cinfo.setSz(sz);
            cinfo.setTaskid(taskid);
            threadglobalName.set(cinfo);
        }

        public static void get(CInfo cinfo){
            threadglobalName.get();
        }
    }

    class CInfo{
        String taskname = "";
        String taskid = "";
        String nsrsbh = "";
        String sz = "";

        public String getTaskname() {
            return taskname;
        }

        public void setTaskname(String taskname) {
            this.taskname = taskname;
        }

        public String getTaskid() {
            return taskid;
        }

        public void setTaskid(String taskid) {
            this.taskid = taskid;
        }

        public String getNsrsbh() {
            return nsrsbh;
        }

        public void setNsrsbh(String nsrsbh) {
            this.nsrsbh = nsrsbh;
        }

        public String getSz() {
            return sz;
        }

        public void setSz(String sz) {
            this.sz = sz;
        }
    }