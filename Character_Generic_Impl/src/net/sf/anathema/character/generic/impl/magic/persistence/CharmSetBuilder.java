package net.sf.anathema.character.generic.impl.magic.persistence;

import static net.sf.anathema.character.generic.impl.magic.ICharmXMLConstants.ATTRIB_ID;
import static net.sf.anathema.character.generic.impl.magic.ICharmXMLConstants.TAG_ALTERNATIVE;
import static net.sf.anathema.character.generic.impl.magic.ICharmXMLConstants.TAG_ALTERNATIVES;
import static net.sf.anathema.character.generic.impl.magic.ICharmXMLConstants.TAG_CHARM;
import static net.sf.anathema.character.generic.impl.magic.ICharmXMLConstants.TAG_CHARM_REFERENCE;
import static net.sf.anathema.character.generic.impl.magic.ICharmXMLConstants.TAG_GENERIC_CHARM;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.disy.commons.core.util.Ensure;
import net.sf.anathema.character.generic.impl.magic.Charm;
import net.sf.anathema.character.generic.impl.magic.persistence.builder.ComboRulesBuilder;
import net.sf.anathema.character.generic.impl.magic.persistence.builder.GenericComboRulesBuilder;
import net.sf.anathema.character.generic.impl.magic.persistence.builder.GenericIdStringBuilder;
import net.sf.anathema.character.generic.impl.magic.persistence.builder.IdStringBuilder;
import net.sf.anathema.character.generic.impl.magic.persistence.builder.prerequisite.AttributeRequirementBuilder;
import net.sf.anathema.character.generic.impl.magic.persistence.builder.prerequisite.GenericAttributeRequirementBuilder;
import net.sf.anathema.character.generic.impl.magic.persistence.builder.prerequisite.GenericTraitPrerequisitesBuilder;
import net.sf.anathema.character.generic.impl.magic.persistence.builder.prerequisite.TraitPrerequisitesBuilder;
import net.sf.anathema.character.generic.magic.ICharm;
import net.sf.anathema.character.generic.magic.charms.ICharmAlternative;
import net.sf.anathema.character.generic.traits.IGenericTrait;
import net.sf.anathema.character.generic.traits.ITraitType;
import net.sf.anathema.lib.exception.PersistenceException;
import net.sf.anathema.lib.xml.ElementUtilities;

import org.dom4j.Document;
import org.dom4j.Element;

public class CharmSetBuilder implements ICharmSetBuilder {

  private final ICharmBuilder builder = new CharmBuilder(
      new IdStringBuilder(),
      new TraitPrerequisitesBuilder(),
      new AttributeRequirementBuilder(),
      new ComboRulesBuilder());
  private final GenericCharmBuilder genericsBuilder = new GenericCharmBuilder(
      new GenericIdStringBuilder(),
      new GenericTraitPrerequisitesBuilder(),
      new GenericAttributeRequirementBuilder(),
      new GenericComboRulesBuilder());

  public ICharm[] buildCharms(Document charmDoc, List<ICharm> existingCharms) throws PersistenceException {
    // TODO NOW: Hier kann man die Reihenfolge richtig drehen
    Set<Charm> allCharms = new HashSet<Charm>();
    Map<String, Charm> charmsById = new HashMap<String, Charm>();
    for (ICharm charm : existingCharms) {
      Charm clone = ((Charm) charm).cloneUnconnected();
      allCharms.add(clone);
      charmsById.put(clone.getId(), clone);
    }
    Element charmListElement = charmDoc.getRootElement();
    for (Element charmElementObject : ElementUtilities.elements(charmListElement, TAG_CHARM)) {
      createCharm(allCharms, charmsById, builder, charmElementObject);
    }
    buildGenericCharms(allCharms, charmsById, charmListElement);
    extractParents(charmsById, allCharms);
    readAlternatives(charmsById, charmListElement);
    return allCharms.toArray(new ICharm[allCharms.size()]);
  }

  private void buildGenericCharms(Set<Charm> allCharms, Map<String, Charm> charmsById, Element charmListElement)
      throws PersistenceException {
    final List<Element> elements = ElementUtilities.elements(charmListElement, TAG_GENERIC_CHARM);
    if (elements.isEmpty()) {
      return;
    }
    final ITraitType[] types = createTraitList(allCharms);
    for (ITraitType type : types) {
      genericsBuilder.setType(type);
      for (Element charmElementObject : elements) {
        createCharm(allCharms, charmsById, genericsBuilder, charmElementObject);
      }
    }
  }

  private ITraitType[] createTraitList(Set<Charm> allCharms) {
    Set<ITraitType> types = new HashSet<ITraitType>();
    for (Charm charm : allCharms) {
      final IGenericTrait[] prerequisites = charm.getPrerequisites();
      if (prerequisites.length > 0) {
        types.add(prerequisites[0].getType());
      }
    }
    return types.toArray(new ITraitType[types.size()]);
  }

  private void createCharm(
      Set<Charm> allCharms,
      Map<String, Charm> charmsById,
      ICharmBuilder currentbuilder,
      Element charmElement) throws PersistenceException {
    Charm newCharm = currentbuilder.buildCharm(charmElement);
    if (allCharms.contains(newCharm)) {
      allCharms.remove(newCharm);
    }
    allCharms.add(newCharm);
    charmsById.put(newCharm.getId(), newCharm);
  }

  private void extractParents(Map<String, Charm> charmsById, Set<Charm> allCharms) {
    for (Charm charm : allCharms) {
      charm.extractParentCharms(charmsById);
    }
  }

  private void readAlternatives(Map<String, Charm> charmsById, Element charmListElement) {
    Element alternativesElement = charmListElement.element(TAG_ALTERNATIVES);
    if (alternativesElement == null) {
      return;
    }
    for (Element alternativeElement : ElementUtilities.elements(alternativesElement, TAG_ALTERNATIVE)) {
      readAlternative(alternativeElement, charmsById);
    }
  }

  private void readAlternative(Element alternativeElement, Map<String, Charm> charmsById) {
    List<Element> charmReferences = ElementUtilities.elements(alternativeElement, TAG_CHARM_REFERENCE);
    Charm[] charms = new Charm[charmReferences.size()];
    for (int index = 0; index < charms.length; index++) {
      String charmId = charmReferences.get(index).attributeValue(ATTRIB_ID);
      Charm charm = charmsById.get(charmId);
      Ensure.ensureNotNull("Charm not found " + charmId, charm); //$NON-NLS-1$
      charms[index] = charm;
    }
    ICharmAlternative charmAlternative = new CharmAlternative(charms);
    for (Charm charm : charms) {
      charm.addAlternative(charmAlternative);
    }
  }
}