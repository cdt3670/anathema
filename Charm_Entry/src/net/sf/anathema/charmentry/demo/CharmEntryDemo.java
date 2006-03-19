package net.sf.anathema.charmentry.demo;

import net.sf.anathema.charmentry.CharmEntryPresenter;
import net.sf.anathema.charmentry.model.CharmEntryModel;
import net.sf.anathema.charmentry.view.CharmEntryView;
import net.sf.anathema.framework.resources.AnathemaResources;
import de.jdemo.extensions.SwingDemoCase;

public class CharmEntryDemo extends SwingDemoCase {

  public void demoCharmEntryInterface() {
    CharmEntryView view = new CharmEntryView();
    CharmEntryModel model = new CharmEntryModel();
    new CharmEntryPresenter(model, view, new AnathemaResources()).initPresentation();
    show(view.getContent());
  }
}