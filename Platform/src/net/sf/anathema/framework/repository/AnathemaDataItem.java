package net.sf.anathema.framework.repository;

import com.google.common.base.Preconditions;
import net.sf.anathema.framework.item.IItemType;
import net.sf.anathema.framework.itemdata.model.IItemData;
import net.sf.anathema.framework.presenter.itemmanagement.PrintNameAdjuster;
import net.sf.anathema.lib.control.IChangeListener;
import net.sf.anathema.lib.util.Identified;

public class AnathemaDataItem extends AbstractAnathemaItem {

  private final IItemData itemData;

  public AnathemaDataItem(IItemType type, IItemData itemData) {
    super(type);
    Preconditions.checkArgument(type.supportsRepository());
    Preconditions.checkNotNull(itemData); //$NON-NLS-1$
    this.itemData = itemData;
    itemData.setPrintNameAdjuster(new PrintNameAdjuster(this));
  }

  public AnathemaDataItem(IItemType type, Identified identificate, IItemData itemData) {
    super(type, identificate);
    Preconditions.checkNotNull(itemData);
    this.itemData = itemData;
    itemData.setPrintNameAdjuster(new PrintNameAdjuster(this));
  }

  @Override
  public IItemData getItemData() {
    return itemData;
  }

  @Override
  public boolean isDirty() {
    return itemData.isDirty();
  }

  @Override
  public void setClean() {
    itemData.setClean();
  }

  @Override
  public void addDirtyListener(IChangeListener changeListener) {
    itemData.addDirtyListener(changeListener);
  }

  @Override
  public void removeDirtyListener(IChangeListener changeListener) {
    itemData.removeDirtyListener(changeListener);
  }
}