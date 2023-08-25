package com.kailoslab.ai4x.mail;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MailDetail {
    private String recipient;
    private String subject;
    private String msgBody;
    private List<String> attachment;

    public MailDetail(String recipient, String subject, String msgBody) {
        this(recipient, subject, msgBody, null);
    }
}
