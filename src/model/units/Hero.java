package model.units;

import model.items.IEquipableItem;
import model.map.Location;

/**
 * A <i>Hero</i> is a special kind of unit, the player that defeats this unit wins the game.
 * <p>
 * This unit <b>can only use spear weapons</b>.
 *
 * @author Ignacio Slater Muñoz
 * @since 1.0
 */
public class Hero extends AbstractUnit {

  /**
   * Creates a new Unit.
   *
   * @param hitPoints
   *     the maximum amount of damage a unit can sustain
   * @param movement
   *     the number of panels a unit can move
   */
  public Hero(final int hitPoints, final int movement, final Location location,
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
  public void equipSpear(IEquipableItem item) {

    if (this.getItems().contains(item)) {

      this.setEquippedItem(item);
      item.setOwner(this);
    }
  }

  @Override
  public boolean equalsTo(IUnit unit){

    if(unit instanceof Hero){

      return (this.getMaxHitPoints()==unit.getMaxHitPoints()) &&
              (this.getLocation()==unit.getLocation()) &&
              (this.getLive()==unit.getLive()) &&
              (this.getItems()==unit.getItems());
    }
    return false;
  }
}
