package com.StartupSAAS.email;

import org.springframework.stereotype.Component;

@Component
public class EmailTemplateBuilder {

    public String wrap(String content) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
            </head>
            <body style="margin:0; padding:0; font-family:Arial,sans-serif; background:#f1f5f9;">
                %s
            </body>
            </html>
        """.formatted(content);
    }

    public String button(String text, String url, String color) {
        return """
            <div style="text-align:center; margin:30px 0;">
                <a href="%s"
                   style="background:%s; color:#fff; padding:14px 28px;
                          text-decoration:none; border-radius:8px; font-weight:600;">
                    %s
                </a>
            </div>
        """.formatted(url, color, text);
    }
}
