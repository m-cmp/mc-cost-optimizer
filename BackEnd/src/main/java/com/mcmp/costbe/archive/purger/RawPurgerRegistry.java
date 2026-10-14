package com.mcmp.costbe.archive.purger;

import com.mcmp.costbe.archive.model.Csp;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/** CSP → RawPurger 매핑. Spring 이 List<RawPurger> 주입 → EnumMap 구성(전략 자동 등록). */
@Component
public class RawPurgerRegistry {

    private final Map<Csp, RawPurger> map;

    public RawPurgerRegistry(List<RawPurger> purgers) {
        Map<Csp, RawPurger> m = new EnumMap<>(Csp.class);
        for (RawPurger p : purgers) {
            m.put(p.csp(), p);
        }
        this.map = m;
    }

    public RawPurger get(Csp csp) {
        return map.get(csp);
    }
}
