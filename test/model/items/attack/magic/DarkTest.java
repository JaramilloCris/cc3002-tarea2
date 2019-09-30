package model.items.attack.magic;

import model.items.IEquipableItem;
import model.map.Location;
import model.units.IUnit;
import model.units.Sorcerer;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * Test set for DarkBook
 *
 * @author Cristobal Jaramillo Andrade
 * @since 1.0
 */
class DarkTest extends AbstractTestBook {

    private DarkBook dark;
    private DarkBook wrongDark;
    private Sorcerer sorcerer;

    @Override
    public void setTestItem() {
        expectedName = "Common dark";
        expectedPower = 10;
        expectedMinRange = 1;
        expectedMaxRange = 2;
        dark = new DarkBook(expectedName, expectedPower, expectedMinRange, expectedMaxRange);
    }

    /**
     * Sets up an item with wrong ranges setted.
     */
    @Override
    public void setWrongRangeItem() {
        wrongDark = new DarkBook("Wrong dark", 0, -1, -2);
    }

    /**
     * Sets the unit that will be equipped with the test item
     */
    @Override
    public void setTestUnit() {
        sorcerer = new Sorcerer(10, 5, new Location(0, 0));
    }

    @Override
    public IEquipableItem getWrongTestItem() {
        return wrongDark;
    }

    @Override
    public IEquipableItem getTestItem() {
        return dark;
    }

    /**
     * @return a unit that can equip the item being tested
     */
    @Override
    public IUnit getTestUnit() {
        return sorcerer;
    }

    @Override
    @Test
    public void damageTest(){

        double StrongDamage = getTestItem().attack(getAnima());
        double WeakDamage = getTestItem().attack(getLight());
        double AgainstDark = getTestItem().attack(getDark());
        double AgainstAxe = getTestItem().attack(getAxe());
        double AgainstSpear = getTestItem().attack(getSpear());
        double AgainstSword = getTestItem().attack(getSword());
        double AgainstBow = getTestItem().attack(getBow());
        double AgainstStaff = getTestItem().attack(getStaff());



        assertEquals(StrongDamage, getTestItem().getPower()*1.5);
        assertEquals(WeakDamage, Math.max(getTestItem().getPower() - 20, 0));
        assertEquals(AgainstDark, getTestItem().getPower());
        assertEquals(AgainstAxe, getTestItem().getPower()*1.5);
        assertEquals(AgainstSpear, getTestItem().getPower()*1.5);
        assertEquals(AgainstSword, getTestItem().getPower()*1.5);
        assertEquals(AgainstBow, getTestItem().getPower()*1.5);
        assertEquals(AgainstStaff, getTestItem().getPower()*1.5);


    }
}
