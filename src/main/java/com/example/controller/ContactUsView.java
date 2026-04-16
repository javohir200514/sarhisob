package com.example.controller;

import com.example.companent.MainLayout;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Route(value = "contact-us", layout = MainLayout.class)
@PageTitle("Contact Us | MoneyFlow")
@PermitAll
public class ContactUsView extends VerticalLayout {

    private static final String SUPPORT_EMAIL = "komilbayevj@gmail.com";
    private static final String SUPPORT_PHONE = "+998 97 791 14 01";

    public ContactUsView() {
        setPadding(false);
        setSpacing(false);
        setMargin(false);
        setWidthFull();
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);

        getStyle()
                .set("min-height", "100vh")
                .set("width", "100%")
                .set("margin", "0")
                .set("padding", "0")
                .set("background",
                        "radial-gradient(ellipse 80% 60% at 20% -10%, rgba(59,130,246,0.16) 0%, transparent 60%), " +
                        "radial-gradient(ellipse 60% 50% at 80% 10%, rgba(139,92,246,0.12) 0%, transparent 55%), " +
                        "linear-gradient(160deg, #040816 0%, #081225 45%, #050914 100%)")
                .set("background-attachment", "fixed")
                .set("overflow-x", "hidden")
                .set("position", "relative")
                .set("font-family", "Inter, Segoe UI, Arial, sans-serif");

        Div noise = new Div();
        noise.getStyle()
                .set("position", "fixed")
                .set("inset", "0")
                .set("background-image",
                        "linear-gradient(rgba(96,165,250,0.04) 1px, transparent 1px), " +
                                "linear-gradient(90deg, rgba(96,165,250,0.04) 1px, transparent 1px)")
                .set("background-size", "60px 60px")
                .set("pointer-events", "none")
                .set("z-index", "0");

        Div container = new Div();
        container.setWidthFull();
        container.getStyle()
                .set("position", "relative")
                .set("z-index", "1")
                .set("width", "100%")
                .set("max-width", "1450px")
                .set("margin", "0 auto")
                .set("padding-left", "80px")
                .set("padding-right", "90px")
                .set("padding-top", "60px")
                .set("padding-bottom", "80px")
                .set("box-sizing", "border-box");

        Div hero = buildHero();
        Div grid = buildGrid();

        container.add(hero, grid);
        add(noise, container);

        getElement().executeJs("""
            const mqGrid = window.matchMedia('(max-width: 980px)');
            const mqRow = window.matchMedia('(max-width: 720px)');
            const grid = document.querySelector('[data-cu-grid]');
            const row = document.querySelector('[data-cu-row]');

            function apply() {
                if (grid) {
                    grid.style.gridTemplateColumns = mqGrid.matches
                        ? '1fr'
                        : 'minmax(320px, 0.9fr) minmax(400px, 1.1fr)';
                }
                if (row) {
                    row.style.flexDirection = mqRow.matches ? 'column' : 'row';
                }
            }

            apply();
            mqGrid.addEventListener('change', apply);
            mqRow.addEventListener('change', apply);
        """);
    }

    private Div buildHero() {
        Div hero = new Div();
        hero.setWidthFull();
        hero.getStyle()
                .set("text-align", "center")
                .set("max-width", "820px")
                .set("margin", "0 auto 34px auto")
                .set("padding", "25px 0 8px");


        H1 title = new H1("Savol yoki Taklifingiz  bo‘lsa yozing");
        title.getStyle()
                .set("margin", "0 0 12px 0")
                .set("font-size", "clamp(1.8rem, 3.6vw, 2.7rem)")
                .set("font-weight", "700")
                .set("line-height", "1.1")
                .set("letter-spacing", "-0.025em")
                .set("color", "#f8fafc");

        Paragraph sub = new Paragraph(
                "Taklif, savol yoki muammo bo‘lsa, shu sahifa orqali bog‘lanishingiz mumkin. Imkon qadar tez javob beramiz."
        );
        sub.getStyle()
                .set("margin", "0 auto")
                .set("max-width", "640px")
                .set("font-size", "13px")
                .set("line-height", "1.7")
                .set("color", "#94a3b8");

        hero.add(title, sub);
        return hero;
    }

    private Div buildGrid() {
        Div grid = new Div();
        grid.getElement().setAttribute("data-cu-grid", "true");
        grid.setWidthFull();
        grid.getStyle()
                .set("display", "grid")
                .set("grid-template-columns", "minmax(320px, 0.9fr) minmax(400px, 1.1fr)")
                .set("gap", "20px")
                .set("align-items", "start");

        grid.add(buildInfoCard(), buildFormCard());
        return grid;
    }

    private Div buildInfoCard() {
        Div card = premiumCard();
        card.getStyle()
                .set("padding", "24px")
                .set("display", "flex")
                .set("flex-direction", "column")
                .set("gap", "14px")
                .set("min-height", "100%");

        Div head = new Div();
        head.getStyle()
                .set("display", "flex")
                .set("flex-direction", "column")
                .set("gap", "4px")
                .set("margin-bottom", "4px");

        H2 title = new H2("Aloqa ma’lumotlari");
        title.getStyle()
                .set("margin", "0")
                .set("font-size", "20px")
                .set("font-weight", "700")
                .set("letter-spacing", "-0.02em")
                .set("color", "#eef2ff");

        Paragraph desc = new Paragraph("Biz bilan quyidagi usullar orqali bog‘lanishingiz mumkin.");
        desc.getStyle()
                .set("margin", "0")
                .set("font-size", "12px")
                .set("line-height", "1.65")
                .set("color", "#7f97bc");

        head.add(title, desc);

        Div email = contactInfoBox(
                VaadinIcon.ENVELOPE,
                "Email",
                "Xabar qoldirsangiz, tez orada javob beramiz.",
                SUPPORT_EMAIL,
                "#60a5fa"
        );

        Div phone = contactInfoBox(
                VaadinIcon.PHONE,
                "Telefon",
                "Dushanba–Juma kunlari ishlaymiz.",
                SUPPORT_PHONE,
                "#a78bfa"
        );

        Div note = new Div(new Text(
                "Ko‘pincha bir necha soat ichida javob beramiz. Muammoni aniq yozsangiz, tezroq yordam bera olamiz."
        ));
        note.getStyle()
                .set("padding", "16px 18px")
                .set("border-radius", "16px")
                .set("background", "rgba(255,255,255,0.03)")
                .set("border", "1px solid rgba(255,255,255,0.06)")
                .set("color", "#8ea8cf")
                .set("font-size", "12.5px")
                .set("line-height", "1.75");

        card.add(head, email, phone, note);
        return card;
    }

    private Div buildFormCard() {
        Div card = premiumCard();
        card.getStyle()
                .set("padding", "24px")
                .set("display", "flex")
                .set("flex-direction", "column")
                .set("min-height", "100%");

        Div head = new Div();
        head.getStyle()
                .set("display", "flex")
                .set("flex-direction", "column")
                .set("gap", "4px")
                .set("margin-bottom", "8px");

        H2 title = new H2("Xabar yuborish");
        title.getStyle()
                .set("margin", "0")
                .set("font-size", "24px")
                .set("font-weight", "800")
                .set("letter-spacing", "-0.03em")
                .set("color", "#eef2ff");

        Paragraph sub = new Paragraph("Quyidagi formani to‘ldiring. Xabar email orqali yuborish uchun ochiladi.");
        sub.getStyle()
                .set("margin", "0")
                .set("font-size", "13px")
                .set("line-height", "1.7")
                .set("color", "#7f97bc");

        head.add(title, sub);

        HorizontalLayout row = new HorizontalLayout();
        row.getElement().setAttribute("data-cu-row", "true");
        row.setWidthFull();
        row.setPadding(false);
        row.setSpacing(false);
        row.setAlignItems(FlexComponent.Alignment.START);
        row.getStyle()
                .set("gap", "12px")
                .set("margin", "8px 0 12px 0");

        TextField nameField = styledTextField("Ismingiz", "Ali Valiyev");
        nameField.getElement().setAttribute("autocomplete", "name");

        EmailField emailField = styledEmailField("Email", "ali@example.com");
        emailField.getElement().setAttribute("autocomplete", "email");

        row.add(nameField, emailField);
        row.expand(nameField, emailField);

        TextField subjectField = styledTextField("Mavzu", "Qisqacha yozing");
        subjectField.setWidthFull();
        subjectField.getStyle().set("margin-bottom", "12px");

        TextArea messageField = styledTextArea("Xabar", "Xabaringizni shu yerga yozing");
        messageField.setWidthFull();
        messageField.getStyle().set("margin-bottom", "18px");

        Button sendBtn = new Button("Yuborish");
        sendBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        sendBtn.setWidthFull();
        sendBtn.getStyle()
                .set("height", "50px")
                .set("border-radius", "14px")
                .set("border", "none")
                .set("background", "linear-gradient(135deg, #2563eb 0%, #7c3aed 100%)")
                .set("color", "white")
                .set("font-size", "14px")
                .set("font-weight", "800")
                .set("letter-spacing", "0.01em")
                .set("box-shadow", "0 14px 34px rgba(37,99,235,0.26)")
                .set("cursor", "pointer");

        sendBtn.addClickListener(e -> {
            String name = safe(nameField.getValue());
            String email = safe(emailField.getValue());
            String subject = safe(subjectField.getValue());
            String message = safe(messageField.getValue());

            if (name.isBlank() || email.isBlank() || subject.isBlank() || message.isBlank()) {
                showToast("Iltimos, barcha maydonlarni to‘ldiring.", false);
                return;
            }

            if (!email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
                showToast("Email manzilni to‘g‘ri kiriting.", false);
                return;
            }

            String body = "Ism: " + name + "\nEmail: " + email + "\n\nXabar:\n" + message;
            String mailto = "mailto:" + SUPPORT_EMAIL
                    + "?subject=" + encode(subject)
                    + "&body=" + encode(body);

            getUI().ifPresent(ui -> ui.getPage().open(mailto));
            showToast("Email oynasi ochilmoqda...", true);

            nameField.clear();
            emailField.clear();
            subjectField.clear();
            messageField.clear();
        });

        card.add(head, row, subjectField, messageField, sendBtn);
        return card;
    }

    private Div premiumCard() {
        Div card = new Div();
        card.getStyle()
                .set("position", "relative")
                .set("background", "linear-gradient(145deg, rgba(8,14,30,0.97) 0%, rgba(12,20,40,0.95) 100%)")
                .set("border", "1px solid rgba(96,165,250,0.12)")
                .set("border-radius", "24px")
                .set("box-shadow", "0 24px 60px rgba(0,0,0,0.34), inset 0 1px 0 rgba(255,255,255,0.04)")
                .set("overflow", "hidden");

        Div glow = new Div();
        glow.getStyle()
                .set("position", "absolute")
                .set("width", "180px")
                .set("height", "180px")
                .set("right", "-50px")
                .set("top", "-50px")
                .set("border-radius", "50%")
                .set("background", "radial-gradient(circle, rgba(37,99,235,0.14) 0%, transparent 70%)")
                .set("filter", "blur(20px)")
                .set("pointer-events", "none");

        card.add(glow);
        return card;
    }

    private Div contactInfoBox(VaadinIcon iconType, String titleText, String descText, String value, String color) {
        Div box = new Div();
        box.getStyle()
                .set("padding", "18px 20px")
                .set("border-radius", "18px")
                .set("background", "rgba(255,255,255,0.02)")
                .set("border", "1px solid rgba(255,255,255,0.06)")
                .set("box-sizing", "border-box");

        Div iconWrap = new Div();
        Icon icon = iconType.create();
        icon.setSize("16px");
        icon.setColor(color);
        iconWrap.add(icon);

        iconWrap.getStyle()
                .set("width", "40px")
                .set("height", "40px")
                .set("border-radius", "12px")
                .set("background", "rgba(59,130,246,0.10)")
                .set("border", "1px solid rgba(59,130,246,0.18)")
                .set("display", "flex")
                .set("align-items", "center")
                .set("justify-content", "center")
                .set("margin-bottom", "12px");

        Div itemTitle = new Div(new Text(titleText));
        itemTitle.getStyle()
                .set("font-weight", "700")
                .set("font-size", "14px")
                .set("color", "#eef2ff")
                .set("margin-bottom", "4px");

        Div itemDesc = new Div(new Text(descText));
        itemDesc.getStyle()
                .set("color", "#7f97bc")
                .set("font-size", "12px")
                .set("line-height", "1.6")
                .set("margin-bottom", "10px");


        Div itemValue = new Div(new Text(value));
        itemValue.getStyle()
                .set("font-weight", "700")
                .set("font-size", "13px")
                .set("color", "#cad8f3")
                .set("word-break", "break-word");

        box.add(iconWrap, itemTitle, itemDesc, itemValue);
        return box;
    }

    private TextField styledTextField(String label, String placeholder) {
        TextField field = new TextField(label);
        field.setPlaceholder(placeholder);
        field.setWidthFull();
        applyFieldStyles(field.getStyle());
        return field;
    }

    private EmailField styledEmailField(String label, String placeholder) {
        EmailField field = new EmailField(label);
        field.setPlaceholder(placeholder);
        field.setWidthFull();
        applyFieldStyles(field.getStyle());
        return field;
    }

    private TextArea styledTextArea(String label, String placeholder) {
        TextArea area = new TextArea(label);
        area.setPlaceholder(placeholder);
        area.setMinHeight("150px");
        area.setWidthFull();
        applyFieldStyles(area.getStyle());
        return area;
    }

    private void applyFieldStyles(com.vaadin.flow.dom.Style s) {
        s.set("--vaadin-input-field-background", "rgba(255,255,255,0.04)")
                .set("--vaadin-input-field-value-color", "#eef2ff")
                .set("--vaadin-input-field-label-color", "#6a83aa")
                .set("--vaadin-input-field-placeholder-color", "rgba(106,131,170,0.45)")
                .set("--vaadin-input-field-border-color", "rgba(255,255,255,0.07)")
                .set("--vaadin-input-field-focused-border-color", "rgba(96,165,250,0.45)")
                .set("--lumo-body-text-color", "#eef2ff");
    }

    private void showToast(String message, boolean success) {
        Notification n = Notification.show(message);
        n.addThemeVariants(success ? NotificationVariant.LUMO_SUCCESS : NotificationVariant.LUMO_ERROR);
        n.setPosition(Notification.Position.TOP_CENTER);
        n.setDuration(3200);
        n.getElement().getStyle()
                .set("background", success ? "rgba(16,185,129,0.14)" : "rgba(239,68,68,0.11)")
                .set("border", success ? "1px solid rgba(16,185,129,0.28)" : "1px solid rgba(239,68,68,0.24)")
                .set("color", success ? "#6ee7b7" : "#fca5a5")
                .set("border-radius", "14px")
                .set("font-size", "13px")
                .set("font-weight", "500")
                .set("box-shadow", "0 8px 24px rgba(0,0,0,0.35)");
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private String safe(String value) {
        return value == null ? "" : value.trim();
    }
}