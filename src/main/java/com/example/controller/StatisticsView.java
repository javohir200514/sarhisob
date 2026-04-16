package com.example.controller;

import com.example.companent.MainLayout;
import com.example.dto.ExpenseDTO;
import com.example.service.ExpenseService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.text.DecimalFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Route(value = "statistics", layout = MainLayout.class)
@JavaScript("https://cdn.jsdelivr.net/npm/chart.js@4.4.1/dist/chart.umd.min.js")
@StyleSheet("https://fonts.googleapis.com/css2?family=Instrument+Sans:wght@400;500;600;700&family=Fraunces:ital,opsz,wght@0,9..144,300;0,9..144,600;1,9..144,300&display=swap")
public class StatisticsView extends VerticalLayout implements BeforeEnterObserver {

    private final ExpenseService expenseService;
    private final DecimalFormat money = new DecimalFormat("#,###");

    private final String canvasId = "dailyChartCanvas";
    private final String categoryCanvasId = "categoryChartCanvas";

    private Span totalVal, avgVal, highVal, lowVal, highDate, lowDate;
    private ComboBox<Integer> yearCombo;
    private ComboBox<Month> monthCombo;
    private Grid<DailyRow> breakdownGrid;
    private Button monthlyBtn, yearlyBtn;

    private String chartType = "line";
    private String periodMode = "MONTH";
    private List<ExpenseDTO> allExpenses = new ArrayList<>();
    private List<Map.Entry<LocalDate, Double>> cachedDaily = List.of();

    private static final String PAGE_BG = "#0b1020";
    private static final String CARD_BG = "linear-gradient(180deg, rgba(255,255,255,0.045) 0%, rgba(255,255,255,0.025) 100%)";
    private static final String BORDER = "1px solid rgba(255,255,255,0.075)";
    private static final String TXT1 = "#eef2ff";
    private static final String TXT2 = "#b7c3df";
    private static final String TXT3 = "#7d8cab";
    private static final String TXT4 = "#55627f";

    private static final String ACCENT = "#7c6fff";
    private static final String ACCENT2 = "#a78bfa";
    private static final String AMBER = "#f0a84a";
    private static final String RED = "#f06060";
    private static final String GREEN = "#4ade80";

    private static final String GRAD_ACCENT = "linear-gradient(135deg,#7c6fff 0%,#a78bfa 100%)";
    private static final String SHADOW = "0 12px 38px rgba(0,0,0,0.26)";
    private static final String SHADOW_HV = "0 18px 52px rgba(0,0,0,0.34), 0 0 0 1px rgba(124,111,255,0.10)";

    public StatisticsView(ExpenseService expenseService) {
        this.expenseService = expenseService;

        setSizeFull();
        setPadding(false);
        setSpacing(false);
        setMargin(false);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);

        getStyle()
                .set("background",
                        "radial-gradient(circle at top left, rgba(124,111,255,0.10), transparent 26%)," +
                                "radial-gradient(circle at bottom right, rgba(59,130,246,0.08), transparent 24%)," +
                                PAGE_BG)
                .set("min-height", "100vh")
                .set("width", "100%")
                .set("box-sizing", "border-box")
                .set("font-family", "'Instrument Sans', sans-serif");

        injectGlobalCss();

        VerticalLayout shell = new VerticalLayout();
        shell.setPadding(false);
        shell.setSpacing(false);
        shell.setMargin(false);
        shell.setWidthFull();
        shell.setDefaultHorizontalComponentAlignment(Alignment.STRETCH);
        shell.getStyle()
                .set("width", "100%")
                .set("max-width", "1450px")
                .set("margin", "0 auto")
                .set("padding-left", "80px")
                .set("padding-right", "90px")
                .set("padding-top", "34px")
                .set("padding-bottom", "56px")
                .set("box-sizing", "border-box")
                .set("gap", "20px");

        shell.add(
                buildHeader(),
                buildStatsGrid(),
                buildChartsRow(),
                buildBreakdownSection()
        );

        add(shell);

        allExpenses = expenseService.getAllByCurrentUser();
        initSelectors(allExpenses);
        loadAndRender();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        boolean authenticated = auth != null
                && auth.isAuthenticated()
                && !(auth instanceof AnonymousAuthenticationToken)
                && !"anonymousUser".equals(auth.getName());

        if (!authenticated) {
            event.forwardTo("login");
        }
    }

    private void injectGlobalCss() {
        String css =
                "html,body{background:#0b1020!important;margin:0;padding:0;overflow-x:hidden}" +
                        "vaadin-app-layout,vaadin-app-layout::part(content){background:transparent!important}" +
                        "vaadin-grid{" +
                        "--lumo-base-color:transparent;" +
                        "--lumo-contrast-5pct:rgba(255,255,255,0.025);" +
                        "--lumo-contrast-10pct:rgba(255,255,255,0.05);" +
                        "--lumo-primary-color:#7c6fff;" +
                        "--lumo-body-text-color:#b7c3df;" +
                        "--lumo-secondary-text-color:#7d8cab;" +
                        "--lumo-header-text-color:#6d7a98;" +
                        "border:none!important;background:transparent!important}" +
                        "vaadin-grid::part(header-cell){" +
                        "background:rgba(255,255,255,0.022)!important;" +
                        "border-bottom:1px solid rgba(255,255,255,0.06)!important;" +
                        "padding:12px 18px!important;" +
                        "font-size:0.68rem!important;" +
                        "font-weight:700!important;" +
                        "text-transform:uppercase!important;" +
                        "letter-spacing:0.09em!important;" +
                        "color:#647391!important}" +
                        "vaadin-grid::part(cell){" +
                        "border-bottom:1px solid rgba(255,255,255,0.04)!important;" +
                        "padding:13px 18px!important;" +
                        "color:#ced9ef!important}" +
                        "vaadin-grid::part(row):hover{background:rgba(124,111,255,0.04)!important}" +
                        "vaadin-grid::part(even-row-cell){background:rgba(255,255,255,0.008)!important}" +
                        "vaadin-combo-box{" +
                        "--lumo-base-color:#141a2b;" +
                        "--lumo-body-text-color:#d0d8f0;" +
                        "--lumo-secondary-text-color:#6b7a99;" +
                        "--lumo-contrast-10pct:rgba(255,255,255,0.08);" +
                        "--lumo-primary-color:#7c6fff;" +
                        "--lumo-border-radius-m:12px}" +
                        "vaadin-combo-box::part(input-field){" +
                        "background:#141a2b!important;" +
                        "border:1px solid rgba(255,255,255,0.09)!important;" +
                        "min-height:40px!important;" +
                        "box-shadow:none!important}" +
                        "vaadin-combo-box::part(input-field):focus-within{" +
                        "border-color:rgba(124,111,255,0.40)!important;" +
                        "box-shadow:0 0 0 3px rgba(124,111,255,0.12)!important}" +
                        "::-webkit-scrollbar{width:5px;height:5px;background:transparent}" +
                        "::-webkit-scrollbar-thumb{background:rgba(255,255,255,0.12);border-radius:999px}" +
                        "::-webkit-scrollbar-thumb:hover{background:rgba(255,255,255,0.20)}";

        UI.getCurrent().getPage().executeJs(
                "const old=document.getElementById('sv-css');" +
                        "if(old)old.remove();" +
                        "const s=document.createElement('style');" +
                        "s.id='sv-css';s.textContent=$0;" +
                        "document.head.appendChild(s);", css
        );
    }

    private Component buildHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setPadding(false);
        header.setSpacing(false);
        header.setJustifyContentMode(JustifyContentMode.BETWEEN);
        header.setAlignItems(Alignment.END);
        header.getStyle()
                .set("margin-bottom", "4px")
                .set("gap", "18px")
                .set("flex-wrap", "wrap")
                .set("box-sizing", "border-box");

        VerticalLayout left = new VerticalLayout();
        left.setPadding(false);
        left.setSpacing(false);
        left.getStyle().set("gap", "8px");


        H1 h1 = new H1("Moliyaviy tahlil");
        h1.getStyle()
                .set("font-size", "clamp(1.8rem,3vw,2.4rem)")
                .set("font-weight", "700")
                .set("letter-spacing", "-0.02em")
                .set("line-height", "1.1")
                .set("color", "#e2e8f0");

        Paragraph sub = new Paragraph("Xarajatlarni kuzatish · kategoriyalar · kunlik trendlar");
        sub.getStyle()
                .set("margin", "0")
                .set("color", TXT4)
                .set("font-size", "0.88rem")
                .set("letter-spacing", "0.01em");

        left.add(h1, sub);

        HorizontalLayout controls = buildControls();
        header.add(left, controls);
        header.expand(left);

        return header;
    }

    private HorizontalLayout buildControls() {
        HorizontalLayout wrap = new HorizontalLayout();
        wrap.setAlignItems(Alignment.CENTER);
        wrap.setSpacing(false);
        wrap.getStyle()
                .set("gap", "8px")
                .set("background", "rgba(255,255,255,0.03)")
                .set("border", BORDER)
                .set("border-radius", "16px")
                .set("padding", "8px")
                .set("flex-wrap", "wrap")
                .set("box-sizing", "border-box");

        monthlyBtn = pillButton("Oylik", true);
        yearlyBtn = pillButton("Yillik", false);

        monthlyBtn.addClickListener(e -> {
            periodMode = "MONTH";
            syncPeriod();
        });

        yearlyBtn.addClickListener(e -> {
            periodMode = "YEAR";
            syncPeriod();
        });

        yearCombo = new ComboBox<>();
        yearCombo.setPlaceholder("Yil");
        yearCombo.setWidth("112px");
        styleCombo(yearCombo);
        yearCombo.addValueChangeListener(e -> loadAndRender());

        monthCombo = new ComboBox<>();
        monthCombo.setPlaceholder("Oy");
        monthCombo.setWidth("148px");
        styleCombo(monthCombo);
        monthCombo.setItemLabelGenerator(m -> m.getDisplayName(TextStyle.FULL, new Locale("uz")));
        monthCombo.addValueChangeListener(e -> loadAndRender());

        wrap.add(monthlyBtn, yearlyBtn, yearCombo, monthCombo);
        return wrap;
    }

    private Button pillButton(String label, boolean active) {
        Button btn = new Button(label);
        btn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        btn.getStyle()
                .set("border-radius", "999px")
                .set("font-size", "0.78rem")
                .set("font-weight", "700")
                .set("font-family", "'Instrument Sans', sans-serif")
                .set("height", "36px")
                .set("padding", "0 18px")
                .set("cursor", "pointer")
                .set("transition", "all 0.18s ease");
        applyPill(btn, active);
        return btn;
    }

    private void applyPill(Button btn, boolean active) {
        if (active) {
            btn.getStyle()
                    .set("background", GRAD_ACCENT)
                    .set("color", "#ffffff")
                    .set("border", "none")
                    .set("box-shadow", "0 4px 18px rgba(124,111,255,0.28)");
        } else {
            btn.getStyle()
                    .set("background", "transparent")
                    .set("color", TXT3)
                    .set("border", "1px solid rgba(255,255,255,0.07)")
                    .set("box-shadow", "none");
        }
    }

    private void styleCombo(ComboBox<?> combo) {
        combo.getStyle()
                .set("--lumo-base-color", "#141929")
                .set("--lumo-body-text-color", "#d0d8f0")
                .set("--lumo-contrast-10pct", "rgba(255,255,255,0.08)")
                .set("--lumo-border-radius-m", "10px")
                .set("font-size", "0.84rem");
    }

    private Component buildStatsGrid() {
        Div grid = new Div();
        grid.getStyle()
                .set("display", "grid")
                .set("grid-template-columns", "repeat(4,minmax(0,1fr))")
                .set("gap", "16px")
                .set("align-items", "stretch")
                .set("width", "100%")
                .set("box-sizing", "border-box");

        grid.getElement().executeJs(
                "const mq1=window.matchMedia('(max-width:980px)');" +
                        "const mq2=window.matchMedia('(max-width:640px)');" +
                        "const el=this;" +
                        "function a(){" +
                        " if(mq2.matches){el.style.gridTemplateColumns='1fr';}" +
                        " else if(mq1.matches){el.style.gridTemplateColumns='repeat(2,minmax(0,1fr))';}" +
                        " else {el.style.gridTemplateColumns='repeat(4,minmax(0,1fr))';}" +
                        "}" +
                        "a();mq1.addEventListener('change',a);mq2.addEventListener('change',a);"
        );

        totalVal = new Span();
        avgVal = new Span();
        highVal = new Span();
        highDate = new Span();
        lowVal = new Span();
        lowDate = new Span();

        grid.add(
                statCard("Jami xarajat", totalVal, null, "💰", ACCENT, "rgba(124,111,255,0.12)"),
                statCard("O‘rtacha kunlik", avgVal, null, "📈", AMBER, "rgba(240,168,74,0.12)"),
                statCard("Eng ko‘p xarajat kuni", highVal, highDate, "🔺", RED, "rgba(240,96,96,0.12)"),
                statCard("Eng kam xarajat kuni", lowVal, lowDate, "🟢", GREEN, "rgba(74,222,128,0.12)")
        );
        return grid;
    }

    private Div statCard(String label, Span valSpan, Span subSpan,
                         String icon, String color, String iconBg) {
        Div card = new Div();
        card.getStyle()
                .set("background", CARD_BG)
                .set("border", BORDER)
                .set("border-radius", "18px")
                .set("padding", "18px 18px 18px 16px")
                .set("display", "flex")
                .set("align-items", "center")
                .set("gap", "14px")
                .set("min-height", "110px")
                .set("height", "100%")
                .set("box-sizing", "border-box")
                .set("box-shadow", SHADOW)
                .set("transition", "transform 0.20s ease, box-shadow 0.20s ease, border-color 0.20s ease");

        hoverEffect(card);

        Div iconBox = new Div(new Span(icon));
        iconBox.getStyle()
                .set("background", iconBg)
                .set("width", "48px")
                .set("height", "48px")
                .set("border-radius", "13px")
                .set("display", "flex")
                .set("align-items", "center")
                .set("justify-content", "center")
                .set("font-size", "1.08rem")
                .set("border", "1px solid rgba(255,255,255,0.05)")
                .set("flex-shrink", "0");

        VerticalLayout info = new VerticalLayout();
        info.setPadding(false);
        info.setSpacing(false);
        info.getStyle()
                .set("gap", "4px")
                .set("flex", "1")
                .set("min-width", "0");

        Span lbl = new Span(label);
        lbl.getStyle()
                .set("font-size", "0.67rem")
                .set("font-weight", "700")
                .set("color", TXT4)
                .set("text-transform", "uppercase")
                .set("letter-spacing", "0.09em");

        valSpan.getStyle()
                .set("font-size", "1.18rem")
                .set("font-weight", "700")
                .set("color", TXT1)
                .set("letter-spacing", "-0.02em")
                .set("line-height", "1.2");

        info.add(lbl, valSpan);

        if (subSpan != null) {
            subSpan.getStyle()
                    .set("font-size", "0.72rem")
                    .set("color", TXT4)
                    .set("line-height", "1.25");
            info.add(subSpan);
        }

        card.add(iconBox, info);
        return card;
    }

    private Component buildChartsRow() {
        Div row = new Div();
        row.getStyle()
                .set("display", "grid")
                .set("grid-template-columns", "minmax(0,1.6fr) minmax(320px,1fr)")
                .set("gap", "16px")
                .set("align-items", "stretch")
                .set("width", "100%")
                .set("box-sizing", "border-box");

        row.getElement().executeJs(
                "const mq=window.matchMedia('(max-width:900px)');" +
                        "const el=this;" +
                        "function a(){el.style.gridTemplateColumns=mq.matches?'1fr':'minmax(0,1.6fr) minmax(320px,1fr)';}" +
                        "a();mq.addEventListener('change',a);"
        );

        row.add(buildTrendCard(), buildCategoryCard());
        return row;
    }

    private Div buildTrendCard() {
        Div card = glassCard();
        card.getStyle()
                .set("height", "100%")
                .set("width", "100%");

        HorizontalLayout hdr = new HorizontalLayout();
        hdr.setWidthFull();
        hdr.setPadding(false);
        hdr.setSpacing(false);
        hdr.setJustifyContentMode(JustifyContentMode.BETWEEN);
        hdr.setAlignItems(Alignment.CENTER);
        hdr.getStyle()
                .set("margin-bottom", "16px")
                .set("gap", "10px")
                .set("flex-wrap", "wrap");

        Span title = sectionTitle("Xarajatlar trendi");

        HorizontalLayout toggle = new HorizontalLayout();
        toggle.setSpacing(false);
        toggle.getStyle()
                .set("gap", "4px")
                .set("background", "rgba(255,255,255,0.03)")
                .set("border", "1px solid rgba(255,255,255,0.06)")
                .set("border-radius", "10px")
                .set("padding", "3px");

        Button lineBtn = miniToggle("Chiziq", true);
        Button barBtn = miniToggle("Ustun", false);

        lineBtn.addClickListener(e -> {
            chartType = "line";
            activateMini(lineBtn, true);
            activateMini(barBtn, false);
            renderChart();
        });

        barBtn.addClickListener(e -> {
            chartType = "bar";
            activateMini(barBtn, true);
            activateMini(lineBtn, false);
            renderChart();
        });

        toggle.add(lineBtn, barBtn);
        hdr.add(title, toggle);

        Div chartWrap = new Div();
        chartWrap.setHeight("280px");
        chartWrap.getStyle()
                .set("position", "relative")
                .set("background", "rgba(8,11,20,0.40)")
                .set("border-radius", "14px")
                .set("padding", "10px")
                .set("border", "1px solid rgba(255,255,255,0.04)")
                .set("box-sizing", "border-box")
                .set("width", "100%");
        chartWrap.getElement().setProperty("innerHTML", "<canvas id='" + canvasId + "'></canvas>");

        card.add(hdr, chartWrap);
        return card;
    }

    private Div buildCategoryCard() {
        Div card = glassCard();
        card.getStyle()
                .set("height", "100%")
                .set("width", "100%");

        Span title = sectionTitle("Kategoriyalar");
        title.getStyle().set("margin-bottom", "16px").set("display", "block");

        Div chartWrap = new Div();
        chartWrap.setHeight("280px");
        chartWrap.getStyle()
                .set("position", "relative")
                .set("background", "rgba(8,11,20,0.40)")
                .set("border-radius", "14px")
                .set("padding", "10px")
                .set("border", "1px solid rgba(255,255,255,0.04)")
                .set("box-sizing", "border-box")
                .set("width", "100%");
        chartWrap.getElement().setProperty("innerHTML", "<canvas id='" + categoryCanvasId + "'></canvas>");

        card.add(title, chartWrap);
        return card;
    }

    private Component buildBreakdownSection() {
        Div card = glassCard();
        card.getStyle()
                .set("margin-bottom", "0")
                .set("width", "100%")
                .set("box-sizing", "border-box");

        Span title = sectionTitle("Batafsil tahlil");
        title.getStyle().set("margin-bottom", "16px").set("display", "block");

        breakdownGrid = new Grid<>(DailyRow.class, false);
        breakdownGrid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_ROW_STRIPES);
        breakdownGrid.getStyle()
                .set("background", "transparent")
                .set("--lumo-base-color", "transparent")
                .set("border-radius", "12px")
                .set("overflow", "hidden")
                .set("width", "100%");

        breakdownGrid.addColumn(DailyRow::dateStr)
                .setHeader("Sana")
                .setFlexGrow(1);

        breakdownGrid.addComponentColumn(r -> {
            Span badge = new Span(r.dayName());
            badge.getStyle()
                    .set("display", "inline-flex")
                    .set("padding", "3px 10px")
                    .set("border-radius", "999px")
                    .set("font-size", "0.68rem")
                    .set("font-weight", "700")
                    .set("letter-spacing", "0.06em")
                    .set("background", "rgba(124,111,255,0.10)")
                    .set("color", ACCENT2)
                    .set("border", "1px solid rgba(124,111,255,0.18)");
            return badge;
        }).setHeader("Kun").setFlexGrow(1);

        breakdownGrid.addColumn(r -> money.format(r.total) + " so‘m")
                .setHeader("Miqdor")
                .setFlexGrow(1);

        breakdownGrid.addComponentColumn(r -> {
            Div bg = new Div();
            bg.getStyle()
                    .set("background", "rgba(255,255,255,0.07)")
                    .set("height", "6px")
                    .set("border-radius", "999px")
                    .set("width", "100px")
                    .set("overflow", "hidden");

            Div fill = new Div();
            fill.getStyle()
                    .set("background", "linear-gradient(90deg,#7c6fff,#a78bfa)")
                    .set("height", "100%")
                    .set("border-radius", "999px")
                    .set("width", Math.round(r.percent) + "%");

            bg.add(fill);

            Span pct = new Span(Math.round(r.percent) + "%");
            pct.getStyle()
                    .set("font-size", "0.72rem")
                    .set("color", TXT3)
                    .set("margin-left", "10px")
                    .set("font-weight", "600")
                    .set("min-width", "34px");

            Div wrap = new Div(bg, pct);
            wrap.getStyle()
                    .set("display", "flex")
                    .set("align-items", "center");
            return wrap;
        }).setHeader("Ulush").setAutoWidth(true);

        card.add(title, breakdownGrid);
        return card;
    }

    private Div glassCard() {
        Div card = new Div();
        card.getStyle()
                .set("background", CARD_BG)
                .set("border", BORDER)
                .set("border-radius", "20px")
                .set("padding", "22px")
                .set("box-sizing", "border-box")
                .set("box-shadow", SHADOW)
                .set("transition", "transform 0.20s ease, box-shadow 0.20s ease, border-color 0.20s ease");
        hoverEffect(card);
        return card;
    }

    private void hoverEffect(Div card) {
        card.getElement().addEventListener("mouseenter", e ->
                card.getStyle()
                        .set("transform", "translateY(-3px)")
                        .set("box-shadow", SHADOW_HV)
                        .set("border-color", "rgba(255,255,255,0.11)"));

        card.getElement().addEventListener("mouseleave", e ->
                card.getStyle()
                        .set("transform", "translateY(0)")
                        .set("box-shadow", SHADOW)
                        .set("border-color", "rgba(255,255,255,0.075)"));
    }

    private Span sectionTitle(String text) {
        Span s = new Span(text);
        s.getStyle()
                .set("font-size", "0.70rem")
                .set("font-weight", "700")
                .set("color", TXT3)
                .set("text-transform", "uppercase")
                .set("letter-spacing", "0.09em");
        return s;
    }

    private Button miniToggle(String label, boolean active) {
        Button b = new Button(label);
        b.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        b.getStyle()
                .set("padding", "4px 11px")
                .set("border-radius", "7px")
                .set("font-size", "0.72rem")
                .set("font-weight", "700")
                .set("cursor", "pointer")
                .set("font-family", "'Instrument Sans', sans-serif")
                .set("transition", "all 0.14s ease");
        activateMini(b, active);
        return b;
    }

    private void activateMini(Button b, boolean active) {
        if (active) {
            b.getStyle()
                    .set("background", "rgba(124,111,255,0.18)")
                    .set("color", ACCENT2)
                    .set("border", "1px solid rgba(124,111,255,0.28)");
        } else {
            b.getStyle()
                    .set("background", "transparent")
                    .set("color", TXT4)
                    .set("border", "1px solid rgba(255,255,255,0.06)");
        }
    }

    private void syncPeriod() {
        applyPill(monthlyBtn, "MONTH".equals(periodMode));
        applyPill(yearlyBtn, "YEAR".equals(periodMode));
        monthCombo.setVisible("MONTH".equals(periodMode));
        loadAndRender();
    }

    private void initSelectors(List<ExpenseDTO> expenses) {
        Set<Integer> years = expenses.stream()
                .filter(e -> e.getDate() != null)
                .map(e -> e.getDate().getYear())
                .collect(Collectors.toCollection(TreeSet::new));

        if (years.isEmpty()) {
            years.add(LocalDate.now().getYear());
        }

        yearCombo.setItems(years);
        yearCombo.setValue(LocalDate.now().getYear());

        monthCombo.setItems(Month.values());
        monthCombo.setValue(LocalDate.now().getMonth());
    }

    private void loadAndRender() {
        Integer year = yearCombo.getValue();
        Month month = monthCombo.getValue();
        if (year == null) return;

        List<ExpenseDTO> filtered = allExpenses.stream()
                .filter(e -> e.getDate() != null && e.getDate().getYear() == year)
                .filter(e -> !"MONTH".equals(periodMode) || e.getDate().getMonth() == month)
                .toList();

        renderCategoryChart(filtered);

        if (filtered.isEmpty()) {
            totalVal.setText("0 so‘m");
            avgVal.setText("0 so‘m");
            highVal.setText("0 so‘m");
            highDate.setText("—");
            lowVal.setText("0 so‘m");
            lowDate.setText("—");
            breakdownGrid.setItems(Collections.emptyList());
            cachedDaily = List.of();
            renderChart();
            return;
        }

        if ("MONTH".equals(periodMode)) {
            renderMonthly(filtered);
        } else {
            renderYearly(filtered);
        }
    }

    private void renderMonthly(List<ExpenseDTO> expenses) {
        Map<LocalDate, Double> sumByDate = expenses.stream()
                .collect(Collectors.groupingBy(
                        ExpenseDTO::getDate,
                        Collectors.summingDouble(e -> e.getAmount().doubleValue())
                ));

        List<Map.Entry<LocalDate, Double>> daily = sumByDate.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .toList();

        double total = daily.stream().mapToDouble(Map.Entry::getValue).sum();

        totalVal.setText(money.format(total) + " so‘m");
        avgVal.setText(money.format(total / Math.max(daily.size(), 1)) + " so‘m");

        daily.stream().max(Map.Entry.comparingByValue()).ifPresent(e -> {
            highVal.setText(money.format(e.getValue()) + " so‘m");
            highDate.setText(formatDateUz(e.getKey()));
        });

        daily.stream().min(Map.Entry.comparingByValue()).ifPresent(e -> {
            lowVal.setText(money.format(e.getValue()) + " so‘m");
            lowDate.setText(formatDateUz(e.getKey()));
        });

        List<DailyRow> rows = daily.stream()
                .map(e -> new DailyRow(e.getKey(), e.getValue(), (e.getValue() / total) * 100))
                .toList();

        breakdownGrid.setItems(rows);
        cachedDaily = daily;
        renderChart();
    }

    private void renderYearly(List<ExpenseDTO> expenses) {
        Map<Month, Double> sumByMonth = expenses.stream()
                .collect(Collectors.groupingBy(
                        e -> e.getDate().getMonth(),
                        Collectors.summingDouble(e -> e.getAmount().doubleValue())
                ));

        List<Map.Entry<Month, Double>> monthly = sumByMonth.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .toList();

        double total = monthly.stream().mapToDouble(Map.Entry::getValue).sum();

        totalVal.setText(money.format(total) + " so‘m");
        avgVal.setText(money.format(total / 12) + " so‘m");

        monthly.stream().max(Map.Entry.comparingByValue()).ifPresent(e -> {
            highVal.setText(money.format(e.getValue()) + " so‘m");
            highDate.setText(e.getKey().getDisplayName(TextStyle.FULL, new Locale("uz")));
        });

        monthly.stream().min(Map.Entry.comparingByValue()).ifPresent(e -> {
            lowVal.setText(money.format(e.getValue()) + " so‘m");
            lowDate.setText(e.getKey().getDisplayName(TextStyle.FULL, new Locale("uz")));
        });

        List<DailyRow> rows = monthly.stream()
                .map(e -> new DailyRow(
                        LocalDate.of(LocalDate.now().getYear(), e.getKey(), 1),
                        e.getValue(),
                        (e.getValue() / total) * 100,
                        e.getKey().getDisplayName(TextStyle.FULL, new Locale("uz"))
                ))
                .toList();

        breakdownGrid.setItems(rows);
        cachedDaily = monthly.stream()
                .map(e -> Map.entry(LocalDate.of(LocalDate.now().getYear(), e.getKey(), 1), e.getValue()))
                .toList();

        renderChart();
    }

    private void renderChart() {
        List<String> labels = cachedDaily.stream()
                .map(e -> {
                    if ("YEAR".equals(periodMode)) {
                        return e.getKey().getMonth().getDisplayName(TextStyle.SHORT, new Locale("uz"));
                    }
                    return formatDateShortUz(e.getKey());
                })
                .toList();

        List<Double> values = cachedDaily.stream()
                .map(Map.Entry::getValue)
                .toList();

        UI.getCurrent().getPage().executeJs(
                "const canvas=document.getElementById($0);" +
                        "if(!canvas)return;" +
                        "const ctx=canvas.getContext('2d');" +
                        "if(window._chart1)window._chart1.destroy();" +
                        "const isBar=$1==='bar';" +
                        "window._chart1=new Chart(ctx,{" +
                        "type:$1," +
                        "data:{" +
                        "labels:$2," +
                        "datasets:[{" +
                        "label:'Xarajatlar',data:$3," +
                        "borderColor:'#9b93ff'," +
                        "backgroundColor:isBar?'rgba(124,111,255,0.52)':'rgba(124,111,255,0.08)'," +
                        "borderRadius:isBar?7:0," +
                        "fill:!isBar,tension:0.42," +
                        "pointBackgroundColor:'#7c6fff'," +
                        "pointBorderColor:'#c4b5fd'," +
                        "pointRadius:3,pointHoverRadius:5,borderWidth:1.5" +
                        "}]" +
                        "}," +
                        "options:{responsive:true,maintainAspectRatio:false," +
                        "plugins:{legend:{display:false}}," +
                        "scales:{" +
                        "x:{grid:{color:'rgba(255,255,255,0.04)'},border:{color:'rgba(255,255,255,0.05)'},ticks:{color:'#55627f',font:{size:10,family:'Instrument Sans'}}}," +
                        "y:{grid:{color:'rgba(255,255,255,0.04)'},border:{color:'rgba(255,255,255,0.05)'},ticks:{color:'#55627f',font:{size:10,family:'Instrument Sans'}}}" +
                        "}" +
                        "}" +
                        "});",
                canvasId, chartType, labels, values
        );
    }

    private void renderCategoryChart(List<ExpenseDTO> filtered) {
        Map<String, Double> catData = filtered.stream()
                .collect(Collectors.groupingBy(
                        e -> getCategoryLabelUz(e.getCategory()),
                        Collectors.summingDouble(e -> e.getAmount().doubleValue())
                ));

        UI.getCurrent().getPage().executeJs(
                "const canvas=document.getElementById($0);" +
                        "if(!canvas)return;" +
                        "const ctx=canvas.getContext('2d');" +
                        "if(window._chart2)window._chart2.destroy();" +
                        "window._chart2=new Chart(ctx,{" +
                        "type:'doughnut'," +
                        "data:{" +
                        "labels:$1," +
                        "datasets:[{" +
                        "data:$2," +
                        "backgroundColor:['#7c6fff','#a78bfa','#c4b5fd','#1d9e75','#f0a84a','#f06060','#06b6d4','#f97316','#4ade80','#818cf8']," +
                        "borderWidth:0,hoverOffset:5" +
                        "}]" +
                        "}," +
                        "options:{responsive:true,maintainAspectRatio:false,cutout:'68%'," +
                        "plugins:{legend:{position:'bottom',labels:{color:'#55627f',font:{size:10,family:'Instrument Sans'},padding:10,boxWidth:9,boxHeight:9}}}" +
                        "}" +
                        "});",
                categoryCanvasId,
                catData.keySet().toArray(),
                catData.values().toArray()
        );
    }

    private String getCategoryLabelUz(String cat) {
        if (cat == null || cat.isBlank()) return "Boshqa";

        return switch (cat.trim().toLowerCase(Locale.ROOT)) {
            case "education", "ta'lim" -> "Ta'lim";
            case "food", "ovqat" -> "Ovqat";
            case "transport" -> "Transport";
            case "utilities", "kommunal" -> "Kommunal";
            case "rent", "ijara" -> "Ijara";
            case "internet" -> "Internet";
            case "healthcare", "sog‘liq", "sog'liq" -> "Sog‘liq";
            case "entertainment", "ko‘ngilochar", "ko'ngilochar" -> "Ko‘ngilochar";
            case "clothing", "kiyim" -> "Kiyim";
            case "travel", "sayohat" -> "Sayohat";
            case "gifts", "sovg‘a", "sovg'a" -> "Sovg‘a";
            case "investment", "investitsiya" -> "Investitsiya";
            case "savings", "jamg‘arma", "jamg'arma" -> "Jamg‘arma";
            case "tobacco & alcohol", "zararli odatlar" -> "Zararli odatlar";
            default -> "Boshqa";
        };
    }

    private String formatDateUz(LocalDate date) {
        return date.getDayOfMonth() + " " +
                date.getMonth().getDisplayName(TextStyle.FULL, new Locale("uz")) + " " +
                date.getYear();
    }

    private String formatDateShortUz(LocalDate date) {
        return date.getDayOfMonth() + " " +
                date.getMonth().getDisplayName(TextStyle.SHORT, new Locale("uz"));
    }

    private String getDayNameUz(DayOfWeek day) {
        return switch (day) {
            case MONDAY -> "Dushanba";
            case TUESDAY -> "Seshanba";
            case WEDNESDAY -> "Chorshanba";
            case THURSDAY -> "Payshanba";
            case FRIDAY -> "Juma";
            case SATURDAY -> "Shanba";
            case SUNDAY -> "Yakshanba";
        };
    }

    static class DailyRow {
        LocalDate date;
        double total;
        double percent;
        String customLabel;

        DailyRow(LocalDate d, double t, double p) {
            date = d;
            total = t;
            percent = p;
        }

        DailyRow(LocalDate d, double t, double p, String l) {
            this(d, t, p);
            customLabel = l;
        }

        public String dateStr() {
            return customLabel != null ? customLabel : date.toString();
        }

        public String dayName() {
            return customLabel != null ? "" : switch (date.getDayOfWeek()) {
                case MONDAY -> "Dushanba";
                case TUESDAY -> "Seshanba";
                case WEDNESDAY -> "Chorshanba";
                case THURSDAY -> "Payshanba";
                case FRIDAY -> "Juma";
                case SATURDAY -> "Shanba";
                case SUNDAY -> "Yakshanba";
            };
        }
    }
}