package net.sf.anathema.lib.compare;

import com.google.common.base.Preconditions;
import net.sf.anathema.lib.resources.IResources;
import net.sf.anathema.lib.util.Identified;

import java.util.Arrays;
import java.util.Comparator;

public class I18nedIdentificateSorter<T extends Identified> {

  public T[] sortAscending(T[] originalGroup, T[] emptyArray, final IResources resources) {
    Preconditions.checkState(originalGroup.length == emptyArray.length, "Arrays must be of equal length"); //$NON-NLS-1$
    System.arraycopy(originalGroup, 0, emptyArray, 0, emptyArray.length);
    Arrays.sort(emptyArray, new Comparator<T>() {
      @Override
      public int compare(T group1, T group2) {
        String firstGroupName = getString(resources, group1);
        String secondGroupName = getString(resources, group2);
        return firstGroupName.compareToIgnoreCase(secondGroupName);
      }
    });
    return emptyArray;
  }

  protected String getString(IResources resources, T group1) {
    return resources.getString(group1.getId());
  }
}