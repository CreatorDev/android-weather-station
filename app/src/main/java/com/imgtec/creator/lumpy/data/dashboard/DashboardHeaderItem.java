package com.imgtec.creator.lumpy.data.dashboard;


public class DashboardHeaderItem extends DashboardItem {

  private String title;
  private Long userTag;
  private final boolean editable;

  public DashboardHeaderItem(String title, boolean editable) {
    this.title = title;
    this.editable = editable;
  }

  public String getTitle() {
    return title;
  }

  @Override
  public int getItemViewType() {
    return VIEW_TYPE_HEADER;
  }

  @Override
  public long getItemID() {
    return title.hashCode();
  }

  public Long getUserTag() {
    return userTag;
  }

  public void setUserTag(Long userTag) {
    this.userTag = userTag;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public boolean isEditable() {
    return editable;
  }
}
