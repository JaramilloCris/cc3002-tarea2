package model.units;

import model.items.IEquipableItem;
import model.map.Location;

/**
 * This class represents a cleric type unit. A cleric can only use staff type weapons, which means
 * that it can receive attacks but can't counter attack any of those.
 *
 * @author Ignacio Slater Muñoz
 * @since 1.0
 */
public class Cleric extends AbstractUnit {

  /**
   * Creates a new Unit.
   *
   * @param hitPoints
   *     the maximum amount of damage a unit can sustain
   * @param movement
   *     the number of panels a unit can move
   */
  public Cleric(final int hitPoints, final int movement, final Location location,
      IEquipableItem... items) {
    super(hitPoints, movement, location, 3, items);
  }

  /**
   * Sets the currently equipped item of this unit.
   *
   * @param item
   *     the item to equip
   */
  @Override
  public void equipStaff(IEquipableItem item) {

    if (this.getItems().contains(item)) {

      this.setEquippedItem(item);
      item.setOwner(this);

    }
  }

  @Override
  public boolean equalsTo(IUnit unit){

    if(unit instanceof Cleric){

      return (this.getMaxHitPoints()==unit.getMaxHitPoints()) &&
              (this.getLocation()==unit.getLocation()) &&
              (this.getLive()==unit.getLive()) &&
              (this.getItems()==unit.getItems());
    }
    return false;
  }

}
