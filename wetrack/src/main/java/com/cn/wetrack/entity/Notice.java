package com.cn.wetrack.entity;

/**
 * Created by fuyzh on 16/8/15.
 */
public class Notice {
    private String NoticeTitle;
    private String NoticeContent;
    private String NoticeData;

    public String getNoticeContent() {
        return NoticeContent;
    }

    public void setNoticeContent(String noticeContent) {
        NoticeContent = noticeContent;
    }

    public String getNoticeData() {
        return NoticeData;
    }

    public void setNoticeData(String noticeData) {
        NoticeData = noticeData;
    }

    public String getNoticeTitle() {
        return NoticeTitle;
    }

    public void setNoticeTitle(String noticeTitle) {
        NoticeTitle = noticeTitle;
    }
}
