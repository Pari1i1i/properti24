package property24.util;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;

/**
 * Standardized collection of modern vector (Lucide / Feather style) SVG icon paths
 * and helper methods for building clean badges, icon labels, and buttons.
 */
public final class SvgIcons {

    private SvgIcons() {}

    // ── SVG Path Constants ──────────────────────────────────────────────────
    public static final String DASHBOARD =
        "<rect x='3' y='3' width='7' height='7' rx='1'/><rect x='14' y='3' width='7' height='7' rx='1'/>" +
        "<rect x='3' y='14' width='7' height='7' rx='1'/><rect x='14' y='14' width='7' height='7' rx='1'/>";

    public static final String HOME =
        "<path d='M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z'/><polyline points='9 22 9 12 15 12 15 22'/>";

    public static final String BOX =
        "<path d='M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z'/>" +
        "<polyline points='3.27 6.96 12 12.01 20.73 6.96'/><line x1='12' y1='22.08' x2='12' y2='12'/>";

    public static final String PACKAGE = BOX;

    public static final String CLIPBOARD =
        "<path d='M16 4h2a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2V6a2 2 0 0 1 2-2h2'/>" +
        "<rect x='8' y='2' width='8' height='4' rx='1' ry='1'/>" +
        "<line x1='9' y1='13' x2='15' y2='13'/><line x1='9' y1='17' x2='15' y2='17'/>";

    public static final String CLIPBOARD_CHECK =
        "<rect width='8' height='4' x='8' y='2' rx='1' ry='1'/><path d='M16 4h2a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2V6a2 2 0 0 1 2-2h2'/><path d='m9 14 2 2 4-4'/>";

    public static final String CHECK =
        "<polyline points='20 6 9 17 4 12'/>";

    public static final String CHECK_CIRCLE =
        "<path d='M22 11.08V12a10 10 0 1 1-5.93-9.14'/><polyline points='22 4 12 14.01 9 11.01'/>";

    public static final String X =
        "<line x1='18' y1='6' x2='6' y2='18'/><line x1='6' y1='6' x2='18' y2='18'/>";

    public static final String X_CIRCLE =
        "<circle cx='12' cy='12' r='10'/><line x1='15' y1='9' x2='9' y2='15'/><line x1='9' y1='9' x2='15' y2='15'/>";

    public static final String ALERT_TRIANGLE =
        "<path d='M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z'/>" +
        "<line x1='12' y1='9' x2='12' y2='13'/><line x1='12' y1='17' x2='12.01' y2='17'/>";

    public static final String ALERT_CIRCLE =
        "<circle cx='12' cy='12' r='10'/><line x1='12' y1='8' x2='12' y2='12'/><line x1='12' y1='16' x2='12.01' y2='16'/>";

    public static final String INFO =
        "<circle cx='12' cy='12' r='10'/><line x1='12' y1='16' x2='12' y2='12'/><line x1='12' y1='8' x2='12.01' y2='8'/>";

    public static final String SETTINGS =
        "<circle cx='12' cy='12' r='3'/>" +
        "<path d='M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 0 1-2.83 2.83l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-4 0v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 0 1-2.83-2.83l.06-.06A1.65 1.65 0 0 0 4.68 15a1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1 0-4h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 0 1 2.83-2.83l.06.06A1.65 1.65 0 0 0 9 4.68a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 4 0v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 0 1 2.83 2.83l-.06.06A1.65 1.65 0 0 0 19.4 9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 0 4h-.09a1.65 1.65 0 0 0-1.51 1z'/>";

    public static final String WRENCH =
        "<path d='M14.7 6.3a1 1 0 0 0 0 1.4l1.6 1.6a1 1 0 0 0 1.4 0l3.77-3.77a6 6 0 0 1-7.94 7.94l-6.91 6.91a2.12 2.12 0 0 1-3-3l6.91-6.91a6 6 0 0 1 7.94-7.94l-3.76 3.76z'/>";

    public static final String LOGOUT =
        "<path d='M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4'/><polyline points='16 17 21 12 16 7'/><line x1='21' y1='12' x2='9' y2='12'/>";

    public static final String SEARCH =
        "<circle cx='11' cy='11' r='8'/><line x1='21' y1='21' x2='16.65' y2='16.65'/>";

    public static final String BELL =
        "<path d='M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9'/><path d='M13.73 21a2 2 0 0 1-3.46 0'/>";

    public static final String CLOCK =
        "<circle cx='12' cy='12' r='10'/><polyline points='12 6 12 12 16 14'/>";

    public static final String CALENDAR =
        "<rect x='3' y='4' width='18' height='18' rx='2' ry='2'/><line x1='16' y1='2' x2='16' y2='6'/><line x1='8' y1='2' x2='8' y2='6'/><line x1='3' y1='10' x2='21' y2='10'/>";

    public static final String CALENDAR_CHECK =
        "<rect x='3' y='4' width='18' height='18' rx='2' ry='2'/><line x1='16' y1='2' x2='16' y2='6'/><line x1='8' y1='2' x2='8' y2='6'/><line x1='3' y1='10' x2='21' y2='10'/><path d='m9 16 2 2 4-4'/>";

    public static final String USER =
        "<path d='M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2'/><circle cx='12' cy='7' r='4'/>";

    public static final String USER_CHECK =
        "<path d='M16 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2'/><circle cx='8.5' cy='7' r='4'/><polyline points='17 11 19 13 23 9'/>";

    public static final String MAP_PIN =
        "<path d='M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z'/><circle cx='12' cy='10' r='3'/>";

    public static final String BUILDING =
        "<rect x='4' y='2' width='16' height='20' rx='2' ry='2'/><path d='M9 22v-4h6v4'/><path d='M8 6h.01'/><path d='M16 6h.01'/><path d='M8 10h.01'/><path d='M16 10h.01'/><path d='M8 14h.01'/><path d='M16 14h.01'/>";

    public static final String CAMERA =
        "<path d='M23 19a2 2 0 0 1-2 2H3a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h4l2-3h6l2 3h4a2 2 0 0 1 2 2z'/><circle cx='12' cy='13' r='4'/>";

    public static final String FOLDER =
        "<path d='M22 19a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h5l2 3h9a2 2 0 0 1 2 2z'/>";

    public static final String FOLDER_OPEN =
        "<path d='M22 19a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h5l2 3h9a2 2 0 0 1 2 2z'/><path d='M2 10h20'/><path d='m21.5 13-2.5 7H5l-3-7'/>";

    public static final String TRASH =
        "<polyline points='3 6 5 6 21 6'/><path d='M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2'/><line x1='10' y1='11' x2='10' y2='17'/><line x1='14' y1='11' x2='14' y2='17'/>";

    public static final String ROTATE_CCW =
        "<polyline points='1 4 1 10 7 10'/><path d='M3.51 15a9 9 0 1 0 2.13-9.36L1 10'/>";

    public static final String REFRESH_CW =
        "<polyline points='23 4 23 10 17 10'/><polyline points='1 20 1 14 7 14'/><path d='M3.51 9a9 9 0 0 1 14.85-3.36L23 10M1 14l4.64 4.36A9 9 0 0 0 20.49 15'/>";

    public static final String PLUS =
        "<line x1='12' y1='5' x2='12' y2='19'/><line x1='5' y1='12' x2='19' y2='12'/>";

    public static final String PLUS_CIRCLE =
        "<circle cx='12' cy='12' r='10'/><line x1='12' y1='8' x2='12' y2='16'/><line x1='8' y1='12' x2='16' y2='12'/>";

    public static final String LIST =
        "<line x1='8' y1='6' x2='21' y2='6'/><line x1='8' y1='12' x2='21' y2='12'/><line x1='8' y1='18' x2='21' y2='18'/><line x1='3' y1='6' x2='3.01' y2='6'/><line x1='3' y1='12' x2='3.01' y2='12'/><line x1='3' y1='18' x2='3.01' y2='18'/>";

    public static final String BOOKMARK =
        "<path d='M19 21l-7-5-7 5V5a2 2 0 0 1 2-2h10a2 2 0 0 1 2 2z'/>";

    public static final String FILE_TEXT =
        "<path d='M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z'/><polyline points='14 2 14 8 20 8'/><line x1='16' y1='13' x2='8' y2='13'/><line x1='16' y1='17' x2='8' y2='17'/><polyline points='10 9 9 9 8 9'/>";

    public static final String FILE_CHECK =
        "<path d='M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z'/><polyline points='14 2 14 8 20 8'/><path d='m9 15 2 2 4-4'/>";

    public static final String MESSAGE_SQUARE =
        "<path d='M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z'/>";

    public static final String TAG =
        "<path d='M20.59 13.41l-7.17 7.17a2 2 0 0 1-2.83 0L2 12V2h10l8.59 8.59a2 2 0 0 1 0 2.82z'/><line x1='7' y1='7' x2='7.01' y2='7'/>";

    public static final String EDIT =
        "<path d='M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7'/><path d='M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z'/>";

    public static final String EYE =
        "<path d='M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z'/><circle cx='12' cy='12' r='3'/>";

    public static final String STAR =
        "<polygon points='12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2'/>";

    public static final String ARROW_RIGHT =
        "<line x1='5' y1='12' x2='19' y2='12'/><polyline points='12 5 19 12 12 19'/>";

    public static final String ARROW_LEFT =
        "<line x1='19' y1='12' x2='5' y2='12'/><polyline points='12 19 5 12 12 5'/>";

    public static final String ARROW_UP =
        "<line x1='12' y1='19' x2='12' y2='5'/><polyline points='5 12 12 5 19 12'/>";

    public static final String BAN =
        "<circle cx='12' cy='12' r='10'/><line x1='4.93' y1='4.93' x2='19.07' y2='19.07'/>";

    public static final String LOCK =
        "<rect x='3' y='11' width='18' height='11' rx='2' ry='2'/><path d='M7 11V7a5 5 0 0 1 10 0v4'/>";

    public static final String SHIELD_CHECK =
        "<path d='M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z'/><path d='m9 12 2 2 4-4'/>";

    public static final String BAR_CHART =
        "<line x1='12' y1='20' x2='12' y2='10'/><line x1='18' y1='20' x2='18' y2='4'/><line x1='6' y1='20' x2='6' y2='16'/>";

    public static final String COINS =
        "<circle cx='8' cy='8' r='6'/><path d='M18.09 10.37A6 6 0 1 1 10.34 18'/><path d='M7 6h1v4'/><path d='m16.7 13.3.7.7'/><path d='m14 16 .7.7'/>";

    public static final String SEND =
        "<line x1='22' y1='2' x2='11' y2='13'/><polygon points='22 2 15 22 11 13 2 9 22 2'/>";

    public static final String UPLOAD =
        "<path d='M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4'/><polyline points='17 8 12 3 7 8'/><line x1='12' y1='3' x2='12' y2='15'/>";

    public static final String MONITOR =
        "<rect x='2' y='3' width='20' height='14' rx='2' ry='2'/><line x1='8' y1='21' x2='16' y2='21'/><line x1='12' y1='17' x2='12' y2='21'/>";

    public static final String MIC =
        "<path d='M12 1a3 3 0 0 0-3 3v8a3 3 0 0 0 6 0V4a3 3 0 0 0-3-3z'/><path d='M19 10v2a7 7 0 0 1-14 0v-2'/><line x1='12' y1='19' x2='12' y2='23'/><line x1='8' y1='23' x2='16' y2='23'/>";

    public static final String SPARKLES =
        "<path d='m12 3-1.912 5.813a2 2 0 0 1-1.275 1.275L3 12l5.813 1.912a2 2 0 0 1 1.275 1.275L12 21l1.912-5.813a2 2 0 0 1 1.275-1.275L21 12l-5.813-1.912a2 2 0 0 1-1.275-1.275L12 3Z'/><path d='M5 3v4'/><path d='M19 17v4'/><path d='M3 5h4'/><path d='M17 19h4'/>";


    // ── SVG String Renderers ────────────────────────────────────────────────
    public static String strokeSvg(String pathData, int size, String strokeColor, double strokeWidth) {
        return "<svg width='" + size + "' height='" + size +
               "' viewBox='0 0 24 24' fill='none' stroke='" + strokeColor +
               "' stroke-width='" + strokeWidth +
               "' stroke-linecap='round' stroke-linejoin='round' xmlns='http://www.w3.org/2000/svg'>" +
               pathData + "</svg>";
    }

    public static String strokeSvg(String pathData, int size, String strokeColor) {
        return strokeSvg(pathData, size, strokeColor, 2.0);
    }

    public static String fillSvg(String pathData, int size, String fillColor) {
        return "<svg width='" + size + "' height='" + size +
               "' viewBox='0 0 24 24' fill='" + fillColor +
               "' xmlns='http://www.w3.org/2000/svg'>" +
               pathData + "</svg>";
    }

    // ── Component Helpers ───────────────────────────────────────────────────
    public static Div createIcon(String pathData, int size, String strokeColor) {
        Div icon = new Div();
        icon.getElement().setProperty("innerHTML", strokeSvg(pathData, size, strokeColor));
        icon.getStyle()
                .set("display", "inline-flex")
                .set("align-items", "center")
                .set("justify-content", "center")
                .set("flex-shrink", "0");
        return icon;
    }

    public static Div createFilledIcon(String pathData, int size, String fillColor) {
        Div icon = new Div();
        icon.getElement().setProperty("innerHTML", fillSvg(pathData, size, fillColor));
        icon.getStyle()
                .set("display", "inline-flex")
                .set("align-items", "center")
                .set("justify-content", "center")
                .set("flex-shrink", "0");
        return icon;
    }

    public static Div createBadge(String text, String pathData, String textColor, String bg, String border) {
        Div badge = new Div();
        badge.getStyle()
                .set("display", "inline-flex")
                .set("align-items", "center")
                .set("gap", "5px")
                .set("padding", "3px 9px")
                .set("border-radius", "20px")
                .set("font-size", "11px")
                .set("font-weight", "700")
                .set("font-family", "'Inter',sans-serif")
                .set("background", bg)
                .set("color", textColor);
        if (border != null && !border.isBlank()) {
            badge.getStyle().set("border", "1px solid " + border);
        }
        if (pathData != null && !pathData.isBlank()) {
            badge.add(createIcon(pathData, 12, textColor));
        }
        Span label = new Span(text);
        badge.add(label);
        return badge;
    }

    public static Div createInlineLabel(String text, String pathData, int iconSize, String iconColor, String textColor, String fontSize, String fontWeight) {
        Div row = new Div();
        row.getStyle()
                .set("display", "inline-flex")
                .set("align-items", "center")
                .set("gap", "6px");
        if (pathData != null && !pathData.isBlank()) {
            row.add(createIcon(pathData, iconSize, iconColor));
        }
        Span label = new Span(text);
        label.getStyle()
                .set("color", textColor)
                .set("font-size", fontSize)
                .set("font-weight", fontWeight)
                .set("font-family", "'Inter',sans-serif");
        row.add(label);
        return row;
    }

    public static void attachIconAndText(Button btn, String text, String pathData, int iconSize, String strokeColor, boolean iconLeft) {
        btn.getElement().removeAllChildren();
        if (pathData != null && !pathData.isBlank()) {
            Div icon = createIcon(pathData, iconSize, strokeColor);
            Span lbl = new Span(text);
            lbl.getStyle().set("margin-left", iconLeft ? "6px" : "0").set("margin-right", iconLeft ? "0" : "6px");
            if (iconLeft) {
                btn.getElement().appendChild(icon.getElement(), lbl.getElement());
            } else {
                btn.getElement().appendChild(lbl.getElement(), icon.getElement());
            }
        } else {
            btn.setText(text);
        }
    }

    public static void attachIcon(Button btn, String pathData, int iconSize, String strokeColor) {
        btn.getElement().removeAllChildren();
        Div icon = createIcon(pathData, iconSize, strokeColor);
        btn.getElement().appendChild(icon.getElement());
    }
}
