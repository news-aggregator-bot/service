package bepicky.service.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@Data
@AllArgsConstructor
public class RawNews {
    private final Set<RawNewsNote> notes;

    private final String webReader;
}
