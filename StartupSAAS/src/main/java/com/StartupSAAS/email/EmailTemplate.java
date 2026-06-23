package com.StartupSAAS.email;

import org.springframework.stereotype.Component;

@Component
public class EmailTemplate {


    public String build(
            EmailBranding brand,
            String title,
            String message,
            String buttonText,
            String buttonUrl
    ) {

        String color = brand.getPrimaryColor() != null
                ? brand.getPrimaryColor()
                : "#2563eb";
        return """
                <!DOCTYPE html>
                <html>
                <body style="  margin:0;background:#f1f5f9;font-family:Arial;">
                <table width="100%%">
                <tr>
                <td align="center">
                <div style= "max-width:600px;background:white;border-radius:15px;overflow:hidden;">
                <div style= "background:%s; padding:30px;text-align:center;color:white;">
                <img src="%s"width="100" style="margin-bottom:15px"
                <h2>%s</h2
                </div>
                <div style="padding:35px">
                <p style="font-size:16px"> %s </p>
                <div style="text-align:center;margin:30px">
                <a href="%s"style= "background:%s;color:white;padding:14px 30px; 
                border-radius:8px; text-decoration:none; ">%s</a>
                </div>
                </div> 
                <div style="padding:20px; text-align:center; background:#f8fafc; color:#64748b;">
                © 2026 %s
                </div>
                </div>
                </td>
                </tr>
                </table>
                </body>
                </html>           
                """
                .formatted(
                        color,
                        brand.getLogoUrl(),
                        brand.getCompanyName(),
                        message,
                        buttonUrl,
                        color,
                        buttonText,
                        brand.getCompanyName()
                );
    }
}