package net.sf.anathema.character.library.trait.specialties;

import net.sf.anathema.character.generic.framework.ITraitReference;
import net.sf.anathema.character.generic.framework.additionaltemplate.model.ITraitContext;
import net.sf.anathema.character.generic.impl.traits.SimpleTraitTemplate;
import net.sf.anathema.character.generic.traits.ITraitType;
import net.sf.anathema.character.library.trait.DefaultTrait;
import net.sf.anathema.character.library.trait.FriendlyValueChangeChecker;
import net.sf.anathema.character.library.trait.TraitType;
import net.sf.anathema.character.library.trait.rules.TraitRules;
import net.sf.anathema.character.library.trait.subtrait.AbstractSubTraitContainer;

public class Specialty extends DefaultTrait implements ISpecialty {

  private final String subTraitName;
  private final AbstractSubTraitContainer container;
  private final ITraitReference reference;

  public Specialty(
      AbstractSubTraitContainer container,
      ITraitReference reference,
      String specialtyName,
      ITraitContext context) {
    super(new TraitRules(new TraitType("Specialty"), //$NON-NLS-1$
        SimpleTraitTemplate.createStaticLimitedTemplate(0, 3),
        context.getLimitationContext()), context, new FriendlyValueChangeChecker());
    this.container = container;
    this.reference = reference;
    this.subTraitName = specialtyName;
  }

  @Override
  public String getName() {
    return subTraitName;
  }

  @Override
  public ITraitReference getTraitReference() {
    return reference;
  }

  @Override
  public ITraitType getBasicTraitType() {
    return reference.getTraitType();
  }

  @Override
  public void setCurrentValue(int value) {
    int increment = value - getCurrentValue();
    if (container.getCurrentDotTotal() + increment <= SpecialtiesContainer.ALLOWED_SPECIALTY_COUNT) {
      super.setCurrentValue(value);
    }
    else {
      super.resetCurrentValue();
    }
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Specialty)) {
      return false;
    }
    Specialty other = (Specialty) obj;
    return super.equals(obj) && other.getName().equals(getName()) && other.getBasicTraitType() == getBasicTraitType();
  }

  @Override
  public int hashCode() {
    return getName().hashCode() + getBasicTraitType().hashCode();
  }
}