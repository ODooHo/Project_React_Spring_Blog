package com.dooho.board.api.comment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PatchCommentDto {
    private String commentContent;
    private LocalDate commentWriteDate;
}
