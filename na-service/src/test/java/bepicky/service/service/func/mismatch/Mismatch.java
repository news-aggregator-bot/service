package bepicky.service.service.func.mismatch;

import lombok.Builder;
import lombok.Data;
import bepicky.service.entity.NewsNoteEntity;

import java.util.ArrayList;
import java.util.List;

@Builder
@Data
public class Mismatch {

    private NewsNoteEntity expected;

    private NewsNoteEntity actual;

    @Builder.Default
    private List<String> messages = new ArrayList<>();

    @Override
    public String toString() {
        return new StringBuilder("\n")
            .append("expected:").append(expected).append("\n")
            .append("actual:").append(actual).append("\n")
            .append("msgs:").append(messages).append("\n")
            .toString();
    }
}
