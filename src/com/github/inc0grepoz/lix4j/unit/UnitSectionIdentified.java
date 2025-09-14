package com.github.inc0grepoz.lix4j.unit;

import java.util.LinkedHashMap;

import com.github.inc0grepoz.lix4j.ctx.Identifier;

public class UnitSectionIdentified extends UnitSection
{

    final LinkedHashMap<Identifier, Unit> identified = new LinkedHashMap<>();

    UnitSectionIdentified(UnitSection parent, boolean add)
    {
        super(parent, add);
    }

    UnitSectionIdentified(UnitSection parent)
    {
        super(parent);
    }

}
