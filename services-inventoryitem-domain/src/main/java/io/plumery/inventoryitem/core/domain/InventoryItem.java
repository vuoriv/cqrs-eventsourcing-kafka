package io.plumery.inventoryitem.core.domain;

import io.plumery.core.AggregateRoot;
import io.plumery.core.ID;
import io.plumery.core.exception.InvalidArgumentException;
import io.plumery.core.exception.InvalidStateException;
import io.plumery.inventoryitem.core.domain.event.ItemsCheckedInToInventory;
import io.plumery.inventoryitem.core.domain.event.ItemsRemovedFromInventory;
import io.plumery.inventoryitem.core.events.InventoryItemCreated;
import io.plumery.inventoryitem.core.events.InventoryItemDeactivated;
import io.plumery.inventoryitem.core.events.InventoryItemRenamed;

import static com.google.common.base.Strings.isNullOrEmpty;

public class InventoryItem extends AggregateRoot {
    private Boolean activated;
    private String name;

    @SuppressWarnings("unused")
    private InventoryItem() {}

    public InventoryItem(ID id, String name) {
        InventoryItemCreated e = new InventoryItemCreated()
            .withName(name);
        e.id = id;
        applyChange(e);
    }


    public void changeName(String newName) {
        if (isNullOrEmpty(newName)) throw new InvalidArgumentException("'newName' should be provided", this.id, version);

        InventoryItemRenamed e = new InventoryItemRenamed()
                .withNewName(newName);
        e.id = id;
        applyChange(e);
    }

    public void remove(Integer count) {
        if (count <= 0) throw new InvalidStateException("cant remove negative count from inventory", this.id, version);
        applyChange(new ItemsRemovedFromInventory(id, count));
    }


    public void checkIn(Integer count) {
        if (count <= 0) throw new InvalidStateException("must have a count greater than 0 to add to inventory", this.id, version);
        applyChange(new ItemsCheckedInToInventory(id, count));
    }

    public void deactivate() {
        if(!activated) throw new InvalidStateException("already deactivated", this.id, version);

        InventoryItemDeactivated e = new InventoryItemDeactivated();
        e.id = id;
        applyChange(e);
    }


    private void apply(InventoryItemRenamed e) {
        name = e.getNewName();
    }

    private void apply(InventoryItemCreated e) {
        id = e.id;
        activated = true;
    }

    private void apply(InventoryItemDeactivated e) {
        activated = false;
    }
}
