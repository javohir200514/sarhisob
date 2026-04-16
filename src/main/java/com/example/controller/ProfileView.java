package com.example.controller;

import com.example.companent.MainLayout;
import com.example.dto.PersonalInfoDTO;
import com.example.dto.StatsDTO;
import com.example.service.ProfileSettingService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

import java.text.DecimalFormat;

@Route(value = "profile", layout = MainLayout.class)
@PageTitle("Profile")
@PermitAll
public class ProfileView extends VerticalLayout {

    private final ProfileSettingService profileSettingService;

    public ProfileView(ProfileSettingService profileSettingService) {
        this.profileSettingService = profileSettingService;
        setSizeFull();
        setPadding(false);
        setSpacing(false);
        setAlignItems(Alignment.CENTER);

        // Zamonaviy gradient fon
        getStyle()
                .set("background", "linear-gradient(180deg, #F8FAFC 0%, #F1F5F9 100%)")
                .set("padding", "40px 20px");

        add(createProfileCard());
    }

    private Component createProfileCard() {
        PersonalInfoDTO dto = profileSettingService.getCurrentPersonalInfo();

        // Ma'lumotlarni xavfsiz olish
        String fullName = safe(dto.getName()) + " " + safe(dto.getSurname());
        String email = safe(dto.getUsername());
        
        String avatarSrc = (dto.getPhotoId() != null)
                ? "/profiles/" + dto.getPhotoId()
                : "images/avatar.png";

        VerticalLayout card = new VerticalLayout();
        card.setWidthFull();
        card.setMaxWidth("900px");
        card.setPadding(false);
        card.setSpacing(false);
        card.setAlignItems(Alignment.START);

        // Karta dizayni (Glassmorphism effekti bilan)
        card.getStyle()
                .set("background", "#FFFFFF")
                .set("border-radius", "24px")
                .set("box-shadow", "0 25px 50px -12px rgba(0, 0, 0, 0.08)")
                .set("border", "1px solid #F1F5F9")
                .set("padding", "48px")
                .set("overflow", "hidden");

        // Header qismi: Avatar va Asosiy Ism
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.setSpacing(true);
        header.getStyle().set("gap", "32px");

        Image avatar = new Image(avatarSrc, "avatar");
        avatar.setWidth("140px");
        avatar.setHeight("140px");
        avatar.getStyle()
                .set("border-radius", "50%")
                .set("object-fit", "cover");

        VerticalLayout titleLayout = new VerticalLayout();
        titleLayout.setPadding(false);
        titleLayout.setSpacing(false);

        H2 nameHeading = new H2(fullName);
        nameHeading.getStyle()
                .set("margin", "0")
                .set("font-size", "36px")
                .set("font-weight", "800")
                .set("color", "#1E293B")
                .set("letter-spacing", "-0.025em");

        Paragraph subText = new Paragraph("Foydalanuvchi ma'lumotlari");
        subText.getStyle()
                .set("margin", "4px 0 0 0")
                .set("color", "#64748B")
                .set("font-size", "16px");

        titleLayout.add(nameHeading, subText);
        header.add(avatar, titleLayout);

        // Ma'lumotlar griddi (Ism, Familiya, Email alohida bloklarda)
        HorizontalLayout infoGrid = new HorizontalLayout();
        infoGrid.setWidthFull();
        infoGrid.getStyle().set("margin-top", "40px").set("gap", "20px");

        Div firstNameCard = createDataField("Ism", safe(dto.getName()), VaadinIcon.USER);
        Div lastNameCard = createDataField("Familiya", safe(dto.getSurname()), VaadinIcon.USER_CARD);
        Div emailCard = createDataField("Email Manzil", email, VaadinIcon.ENVELOPE_O);

        infoGrid.add(firstNameCard, lastNameCard, emailCard);
        infoGrid.getChildren().forEach(c -> ((Div)c).getStyle().set("flex", "1"));

        // Action tugmasi
        Button editBtn = new Button("Profilni tahrirlash", VaadinIcon.EDIT.create());
        editBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        editBtn.setWidthFull();
        editBtn.getStyle()
                .set("margin-top", "40px")
                .set("height", "60px")
                .set("border-radius", "16px")
                .set("font-size", "18px")
                .set("font-weight", "600")
                .set("background", "#2563EB")
                .set("box-shadow", "0 10px 15px -3px rgba(37, 99, 235, 0.3)")
                .set("cursor", "pointer");

        editBtn.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate(AccountSettingsView.class)));

        card.add(header, infoGrid, editBtn);
        return card;
    }

    private Div createDataField(String label, String value, VaadinIcon iconType) {
        Div container = new Div();
        container.getStyle()
                .set("background", "#F8FAFC")
                .set("padding", "20px")
                .set("border-radius", "16px")
                .set("border", "1px solid #E2E8F0");

        HorizontalLayout labelLayout = new HorizontalLayout();
        labelLayout.setAlignItems(Alignment.CENTER);
        labelLayout.setSpacing(false);
        labelLayout.getStyle().set("gap", "8px");

        Icon icon = iconType.create();
        icon.setSize("14px");
        icon.getStyle().set("color", "#64748B");

        Paragraph labelTxt = new Paragraph(label);
        labelTxt.getStyle()
                .set("margin", "0")
                .set("font-size", "13px")
                .set("font-weight", "600")
                .set("color", "#64748B")
                .set("text-transform", "uppercase");

        labelLayout.add(icon, labelTxt);

        Paragraph valueTxt = new Paragraph(value);
        valueTxt.getStyle()
                .set("margin", "8px 0 0 0")
                .set("font-size", "18px")
                .set("font-weight", "700")
                .set("color", "#334155");

        container.add(labelLayout, valueTxt);
        return container;
    }

    private String safe(String value) {
        return (value == null || value.isBlank()) ? "-" : value;
    }
}