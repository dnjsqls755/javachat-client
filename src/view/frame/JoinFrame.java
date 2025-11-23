package view.frame;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import javafx.concurrent.Worker;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;
import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilDateModel;
import java.awt.image.BufferedImage;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;
import dto.request.JoinRequest;
import java.io.DataOutputStream;

public class JoinFrame extends JFrame implements ActionListener {

    // ===== UI 컴포넌트 =====
    JTextField idField = new JTextField();
    JButton idCheckBtn = new JButton("중복 확인");

    JPasswordField pwField = new JPasswordField();
    JPasswordField pwConfirmField = new JPasswordField();
    JProgressBar pwStrengthBar = new JProgressBar(0, 100);
    JLabel pwStrengthLabel = new JLabel("비밀번호 강도");

    JTextField nicknameField = new JTextField();
    JButton nicknameCheckBtn = new JButton("중복 확인");

    JTextField emailLocalField = new JTextField();
    JComboBox<String> emailDomainCombo = new JComboBox<>(new String[]{"직접입력", "naver.com", "gmail.com", "daum.net"});

    JTextField nameField = new JTextField();
    JComboBox<String> genderCombo = new JComboBox<>(new String[]{"남", "여", "기타"});

    JDatePickerImpl datePicker;

    JComboBox<String> phonePrefixCombo = new JComboBox<>(new String[]{"010", "011", "016"});
    JTextField phoneField = new JTextField(); // 뒤 8자리 입력

    JTextField postalField = new JTextField();
    JButton postalBtn = new JButton("우편번호");

    JTextField addressField = new JTextField();
    JTextField detailAddressField = new JTextField();

    JLabel profilePreview = new JLabel("사진 없음", SwingConstants.CENTER);
    JButton uploadBtn = new JButton("사진 업로드");

    JButton joinBtn = new JButton("회원가입");
    //JButton backBtn = new JButton("뒤로 가기");

    File profileImageFile = null;

    // ===== 스타일(폰트/여백/색상) =====
    private static final Font FONT_LABEL = new Font("SansSerif", Font.PLAIN, 14);
    private static final Font FONT_INPUT = new Font("SansSerif", Font.PLAIN, 14);
    private static final Insets ROW_INSETS = new Insets(4, 4, 4, 4);
    private static final int FIELD_COLS = 18; // 텍스트필드 폭 균등화

    public JoinFrame() {
        setTitle("회원가입");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(720, 900);
        setLocationRelativeTo(null);

        // 메인 패널 (GridBagLayout)
        JPanel main = new JPanel(new GridBagLayout());
        main.setBorder(new EmptyBorder(16, 16, 16, 16));
        add(main);

        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = ROW_INSETS;
        gc.weightx = 0;

        // 헤더
        JLabel header = new JLabel("회원가입");
        header.setFont(new Font("SansSerif", Font.BOLD, 20));
        gc.gridx = 0; gc.gridy = 0; gc.gridwidth = 3; gc.weightx = 1;
        main.add(header, gc);

        JSeparator sep = new JSeparator();
        gc.gridy = 1;
        main.add(sep, gc);

        // 행 번호 시작
        int row = 2;

        // ===== 각 행 추가 =====
        addRow(main, gc, row++, "아이디", idField, idCheckBtn);
        
        addRow(main, gc, row++, "비밀번호", pwField, null);

        // 비밀번호 확인 + 강도
        JPanel pwConfirmPanel = new JPanel(new BorderLayout(8, 0));
        pwConfirmPanel.add(pwConfirmField, BorderLayout.CENTER);

        JPanel strengthPanel = new JPanel(new BorderLayout(6, 0));
        pwStrengthBar.setStringPainted(true);
        strengthPanel.add(pwStrengthLabel, BorderLayout.WEST);
        strengthPanel.add(pwStrengthBar, BorderLayout.CENTER);

        gc.gridx = 0; gc.gridy = row; gc.gridwidth = 1; gc.weightx = 0;
        main.add(makeLabel("비밀번호 확인"), gc);
        gc.gridx = 1; gc.gridwidth = 1; gc.weightx = 1;
        pwConfirmField.setFont(FONT_INPUT);
        pwConfirmPanel.setPreferredSize(new Dimension(0, pwConfirmField.getPreferredSize().height));
        main.add(pwConfirmPanel, gc);
        gc.gridx = 2; gc.gridwidth = 1; gc.weightx = 0.7;
        main.add(strengthPanel, gc);
        row++;

       //addRow(main, gc, row++, "닉네임", nicknameField, null);
        addRow(main, gc, row++, "닉네임", nicknameField, nicknameCheckBtn);

        // 이메일 (로컬 + @ + 도메인 콤보)
        JPanel emailPanel = new JPanel(new GridBagLayout());
        GridBagConstraints egc = new GridBagConstraints();
        egc.insets = new Insets(0, 0, 0, 6);
        egc.fill = GridBagConstraints.HORIZONTAL;

        emailLocalField.setColumns(FIELD_COLS);
        emailLocalField.setFont(FONT_INPUT);
        egc.gridx = 0; egc.weightx = 1; emailPanel.add(emailLocalField, egc);

        JLabel atLabel = new JLabel("@");
        atLabel.setFont(FONT_LABEL);
        egc.gridx = 1; egc.weightx = 0; emailPanel.add(atLabel, egc);

        emailDomainCombo.setFont(FONT_INPUT);
        egc.gridx = 2; egc.weightx = 0.4; emailPanel.add(emailDomainCombo, egc);

        addRow(main, gc, row++, "이메일", emailPanel, null);

        addRow(main, gc, row++, "이름", nameField, null);

        addRow(main, gc, row++, "성별", genderCombo, null);

        // 생년월일 (JDatePicker, 한국어 라벨)
        UtilDateModel model = new UtilDateModel();
       // Properties p = new Properties();
        //p.put("text.today", "오늘");
       // p.put("text.month", "월");
       // p.put("text.year", "년");
        JDatePanelImpl datePanel = new JDatePanelImpl(model);
        datePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());

        addRow(main, gc, row++, "생년월일", datePicker, null);

        // 전화번호 (prefix + 뒤 8자리)
        JPanel phonePanel = new JPanel(new GridBagLayout());
        GridBagConstraints pgc = new GridBagConstraints();
        pgc.insets = new Insets(0, 0, 0, 6);
        pgc.fill = GridBagConstraints.HORIZONTAL;

        phonePrefixCombo.setFont(FONT_INPUT);
        pgc.gridx = 0; pgc.weightx = 0; phonePanel.add(phonePrefixCombo, pgc);

        JLabel dash = new JLabel("-");
        dash.setFont(FONT_LABEL);
        pgc.gridx = 1; phonePanel.add(dash, pgc);

        phoneField.setColumns(10);
        phoneField.setFont(FONT_INPUT);
        phoneField.setDocument(new NumericDocument(8)); // 숫자 8자리 제한
        pgc.gridx = 2; pgc.weightx = 1; phonePanel.add(phoneField, pgc);

        addRow(main, gc, row++, "전화번호", phonePanel, null);

        // 우편번호 + 버튼
        addRow(main, gc, row++, "우편번호", postalField, postalBtn);
        addRow(main, gc, row++, "주소", addressField, null);
        addRow(main, gc, row++, "상세주소", detailAddressField, null);

        // 프로필 사진 (썸네일 + 업로드)
        profilePreview.setPreferredSize(new Dimension(120, 120));
        profilePreview.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        addRow(main, gc, row++, "프로필 사진", profilePreview, uploadBtn);

        // 하단 버튼
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        btnPanel.add(joinBtn);
       // btnPanel.add(backBtn);

        gc.gridx = 0; gc.gridy = row; gc.gridwidth = 3; gc.weightx = 1;
        main.add(btnPanel, gc);

        // ===== 폰트/공통속성 =====
        for (JComponent c : new JComponent[]{
                idField, pwField, pwConfirmField, nicknameField, emailLocalField,
                nameField, phoneField, postalField, addressField, detailAddressField
        }) {
            c.setFont(FONT_INPUT);
            if (c instanceof JTextField tf) tf.setColumns(FIELD_COLS);
        }

        // ===== 이벤트 등록 =====
        uploadBtn.addActionListener(this);
        postalBtn.addActionListener(this);
        joinBtn.addActionListener(this);
        //backBtn.addActionListener(this);

        idCheckBtn.addActionListener(this);
        nicknameCheckBtn.addActionListener(this);

        // 이메일 도메인 선택 로직
        emailDomainCombo.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                String sel = (String) emailDomainCombo.getSelectedItem();
                if ("직접입력".equals(sel)) {
                    // 도메인은 사용자가 직접 타이핑을 하도록 유도 (로컬 필드만 사용)
                    emailLocalField.requestFocus();
                }
            }
        });

        // 비밀번호 강도 업데이트
        KeyAdapter pwListener = new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                updatePasswordStrength();
            }
        };
        pwField.addKeyListener(pwListener);
        pwConfirmField.addKeyListener(pwListener);

        setVisible(true);
    }

    // ===== 레이아웃 헬퍼: 라벨/필드/오른쪽 컴포넌트 =====
    private void addRow(JPanel main, GridBagConstraints gc, int row,
                        String labelText, JComponent center, JComponent right) {
        JLabel label = makeLabel(labelText);

        gc.gridx = 0; gc.gridy = row; gc.gridwidth = 1; gc.weightx = 0;
        main.add(label, gc);

        gc.gridx = 1; gc.gridwidth = 1; gc.weightx = 1;
        main.add(center, gc);

        if (right != null) {
            gc.gridx = 2; gc.gridwidth = 1; gc.weightx = 0.5;
            main.add(right, gc);
        } else {
            // 빈 공간 확보 (정렬 유지)
            JPanel spacer = new JPanel();
            spacer.setOpaque(false);
            gc.gridx = 2; gc.gridwidth = 1; gc.weightx = 0.3;
            main.add(spacer, gc);
        }
    }

    private JLabel makeLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_LABEL);
        return label;
    }

    // ===== 이벤트 처리 =====
    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == uploadBtn) {
            JFileChooser chooser = new JFileChooser();
            int result = chooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                profileImageFile = chooser.getSelectedFile();
                try {
    BufferedImage originalImage = ImageIO.read(profileImageFile);
    BufferedImage resizedImage = (BufferedImage) scaleImage(originalImage, 120, 120);
    profilePreview.setText(null);
    profilePreview.setIcon(new ImageIcon(resizedImage));
} catch (IOException ex) {
    ex.printStackTrace();
    JOptionPane.showMessageDialog(this, "이미지 로딩 실패: " + ex.getMessage());
}
            }
        } else if (src == postalBtn) {
        	openPostcodeDialog();
        }else if (src == idCheckBtn) {
            String userId = idField.getText().trim();
            if (userId.isEmpty()) {
                JOptionPane.showMessageDialog(this, "아이디를 입력해주세요.");
                idField.requestFocus();
                return;
            }
            try (
                Socket socket = new Socket("localhost", 9000);
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))
            ) {
                writer.println("ID_CHECK:" + userId);
                String response = reader.readLine();

                if ("ID_OK".equals(response)) {
                    JOptionPane.showMessageDialog(this, "사용 가능한 아이디입니다.");
                } else if ("ID_DUPLICATE".equals(response)) {
                    JOptionPane.showMessageDialog(this, "이미 사용 중인 아이디입니다.");
                } else {
                    JOptionPane.showMessageDialog(this, "알 수 없는 응답: " + response);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "서버 연결 실패: " + ex.getMessage());
            }
        }else if (src == nicknameCheckBtn) {
            String nickname = nicknameField.getText().trim();
            if (nickname.isEmpty()) {
                JOptionPane.showMessageDialog(this, "닉네임을 입력해주세요.");
                nicknameField.requestFocus();
                return;
            }

            try (
                Socket socket = new Socket("localhost", 9000);
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))
            ) {
                writer.println("NICKNAME_CHECK:" + nickname);
                String response = reader.readLine();

                if ("NICKNAME_OK".equals(response)) {
                    JOptionPane.showMessageDialog(this, "사용 가능한 닉네임입니다.");
                } else if ("NICKNAME_DUPLICATE".equals(response)) {
                    JOptionPane.showMessageDialog(this, "이미 사용 중인 닉네임입니다.");
                } else {
                    JOptionPane.showMessageDialog(this, "알 수 없는 응답: " + response);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "서버 연결 실패: " + ex.getMessage());
            }
        }else if (src == joinBtn) {
            if (!validateForm()) return;

            // 생년월일
            Date selectedDate = (Date) datePicker.getModel().getValue();
            String birthDate = "";
            if (selectedDate != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                birthDate = sdf.format(selectedDate);
            }

            // 이메일
            String domain = (String) emailDomainCombo.getSelectedItem();
            String email = emailLocalField.getText().trim() + "@" + ("직접입력".equals(domain) ? "" : domain);

            // 전화번호
            String phone = phonePrefixCombo.getSelectedItem() + phoneField.getText().trim();

            // JoinRequest 생성
            JoinRequest req = new JoinRequest();
            req.setUserId(idField.getText().trim());
            req.setName(nameField.getText().trim());
            req.setPassword(new String(pwField.getPassword()));
            req.setProfileImg(profileImageFile != null ? profileImageFile.getName() : "");
            req.setStatusMsg(""); // 상태메시지 입력 필드가 없으므로 빈 값
            req.setNickname(nicknameField.getText().trim());
            req.setEmail(email);
            req.setPhone(phone);
            req.setAddress(addressField.getText().trim());
            req.setDetailAddress(detailAddressField.getText().trim());
            req.setPostalCode(postalField.getText().trim());
            req.setGender((String) genderCombo.getSelectedItem());
            req.setBirthDate(birthDate);
// 서버 전송
try (
    Socket socket = new Socket("localhost", 9000);
    PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
    BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    DataOutputStream dos = new DataOutputStream(socket.getOutputStream())
) {
    writer.println("SIGNUP:" + req.toMessage());

    // 이미지 전송
    if (profileImageFile != null) {
        byte[] imageBytes = getResizedProfileImageBytes(profileImageFile);
        dos.writeInt(imageBytes.length); // 이미지 크기
        dos.write(imageBytes);           // 이미지 데이터
        dos.flush();
    }

    String response = reader.readLine();
    switch (response) {
        case "SIGNUP_SUCCESS" -> JOptionPane.showMessageDialog(this, "회원가입 성공!");
        case "SIGNUP_DUPLICATE_ID" -> JOptionPane.showMessageDialog(this, "이미 존재하는 아이디입니다.");
        case "SIGNUP_INVALID_PASSWORD" -> JOptionPane.showMessageDialog(this, "비밀번호가 유효하지 않습니다.");
        case "SIGNUP_FAIL" -> JOptionPane.showMessageDialog(this, "회원가입 실패.");
        default -> JOptionPane.showMessageDialog(this, "알 수 없는 응답: " + response);
    }
} catch (Exception ex) {
    ex.printStackTrace();
    JOptionPane.showMessageDialog(this, "서버 연결 실패: " + ex.getMessage());
}

          }
       }
 // Daum(카카오) 우편번호 서비스 띄워서 선택 결과를 Swing 필드에 반영
    private void openPostcodeDialog() {
        // 모달 다이얼로그 + JavaFX 패널 생성
        JDialog dialog = new JDialog(this, "우편번호 검색", true);
        JFXPanel jfxPanel = new JFXPanel(); // JavaFX 초기화 트리거
        dialog.setLayout(new BorderLayout());
        dialog.add(jfxPanel, BorderLayout.CENTER);
        dialog.setSize(500, 600);
        dialog.setLocationRelativeTo(this);

        // JavaFX 스레드에서 WebView 구성
        Platform.runLater(() -> {
            WebView webView = new WebView();
            WebEngine webEngine = webView.getEngine();

            // 페이지 로드 완료 후 JS ↔ Java 브릿지 연결
            webEngine.getLoadWorker().stateProperty().addListener((obs, old, state) -> {
                if (state == Worker.State.SUCCEEDED) {
                    // window.app.setAddress(...) 로 Java 메서드 호출 가능하게 설정
                    JSObject window = (JSObject) webEngine.executeScript("window");
                    window.setMember("app", new PostcodeBridge(dialog));
                }
            });

            // 다음 우편번호 서비스 임베드 HTML
            String html = """
                <!DOCTYPE html>
                <html>
                <head>
                  <meta charset="UTF-8">
                  <title>주소 검색</title>
                  <style>
                    html, body { margin:0; padding:0; height:100%; }
                    #wrap { width:100%; height:100%; }
                  </style>
                <script src="https://t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js"></script>
                </head>
                <body>
                  <div id="wrap"></div>
                  <script>
                    (function() {
                      new daum.Postcode({
                        oncomplete: function(data) {
                          var zonecode = data.zonecode || '';
                          var roadAddr = data.roadAddress || '';
                          var jibunAddr = data.jibunAddress || '';
                            
                     
                          // Java 쪽 브릿지로 전송
                          if (window.app && window.app.setAddress) {
                            try {
                              window.app.setAddress(zonecode, roadAddr, jibunAddr);
                            } catch (e) {
                              console.error('주소 전달 실패', e);
                            }
                          }
                        },
                        width: '100%',
                        height: '100%'
                      }).embed(document.getElementById('wrap'));
                    })();
                  </script>
                </body>
                </html>
            """;

            webEngine.loadContent(html);
            jfxPanel.setScene(new Scene(webView));
        });

        dialog.setVisible(true);
    }

    private void applyPostcodeSelection(String zonecode, String roadAddr, String jibunAddr, JDialog dialog) {
        postalField.setText(zonecode != null ? zonecode : "");
        String addr = (roadAddr != null && !roadAddr.isEmpty())
                ? roadAddr
                : (jibunAddr != null ? jibunAddr : "");
        addressField.setText(addr);
        detailAddressField.requestFocus();
        dialog.dispose();
    }

    public class PostcodeBridge {
        private final JDialog dialog;

        public PostcodeBridge(JDialog dialog) {
            this.dialog = dialog;
        }

        public void setAddress(String zonecode, String roadAddr, String jibunAddr) {
            SwingUtilities.invokeLater(() -> applyPostcodeSelection(zonecode, roadAddr, jibunAddr, dialog));
        }
    }

    // ===== 폼 검증 =====
    private boolean validateForm() {
        String id = idField.getText().trim();
        String pw = new String(pwField.getPassword());
        String pwc = new String(pwConfirmField.getPassword());
        String name = nameField.getText().trim();

        if (id.isEmpty()) {
            warn("아이디를 입력하세요.", idField);
            return false;
        }
        if (pw.isEmpty()) {
            warn("비밀번호를 입력하세요.", pwField);
            return false;
        }
        if (!pw.equals(pwc)) {
            warn("비밀번호가 일치하지 않습니다.", pwConfirmField);
            return false;
        }
        if (name.isEmpty()) {
            warn("이름을 입력하세요.", nameField);
            return false;
        }
        if (pwStrengthBar.getValue() < 40) {
            warn("비밀번호 강도가 낮습니다. 숫자/문자/특수문자를 포함하세요.", pwField);
            return false;
        }
        return true;
    }

    private void warn(String msg, JComponent comp) {
        JOptionPane.showMessageDialog(this, msg);
        comp.requestFocus();
    }

    // ===== 비밀번호 강도 평가(간단 규칙) =====
    private void updatePasswordStrength() {
        String pw = new String(pwField.getPassword());
        String pwc = new String(pwConfirmField.getPassword());

        int score = 0;
        if (pw.length() >= 8) score += 25;
        if (pw.matches(".*[A-Z].*")) score += 20;
        if (pw.matches(".*[a-z].*")) score += 20;
        if (pw.matches(".*\\d.*")) score += 20;
        if (pw.matches(".*[^A-Za-z0-9].*")) score += 15;
        if (pw.length() >= 12) score += 10;
        score = Math.min(score, 100);

        pwStrengthBar.setValue(score);
        pwStrengthBar.setString(score + "%");

        if (!pw.isEmpty() && !pwc.isEmpty()) {
            pwStrengthLabel.setText(pw.equals(pwc) ? "비밀번호 강도" : "비밀번호 불일치");
        } else {
            pwStrengthLabel.setText("비밀번호 강도");
        }
    }

    // ===== 숫자 입력 제한 Document =====
    static class NumericDocument extends PlainDocument {
        private final int maxLen;
        NumericDocument(int maxLen) { this.maxLen = maxLen; }
        @Override
        public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
            if (str == null) return;
            String onlyDigits = str.replaceAll("\\D", "");
            if (onlyDigits.isEmpty()) return;
            if (getLength() + onlyDigits.length() > maxLen) {
                onlyDigits = onlyDigits.substring(0, maxLen - getLength());
            }
            super.insertString(offs, onlyDigits, a);
        }
    }

    // ===== 이미지 스케일링(썸네일) =====
    private Image scaleImage(Image src, int w, int h) {
        int sw = src.getWidth(null);
        int sh = src.getHeight(null);
        if (sw <= 0 || sh <= 0) return src;

        double rw = (double) w / sw;
        double rh = (double) h / sh;
        double r = Math.min(rw, rh); // 비율 유지(내접)
        int tw = (int) (sw * r);
        int th = (int) (sh * r);

        Image scaled = src.getScaledInstance(tw, th, Image.SCALE_SMOOTH);

        BufferedImage canvas = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = canvas.createGraphics();
        g2.setComposite(AlphaComposite.Src);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, w, h);

        // ★ 중앙 정렬 
        int x = (w - tw) / 2; // 중앙 정렬
        int y = (h - th) / 2; // 중앙 정렬
      

        g2.drawImage(scaled, x, y, null);
        g2.dispose();
        return canvas;
    }
    //이미지 리사이징
     private byte[] getResizedProfileImageBytes(File originalFile) {
         try {
             ImageIcon icon = new ImageIcon(originalFile.getAbsolutePath());
             Image resized = scaleImage(icon.getImage(), 200, 200);

             BufferedImage buffered = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
             Graphics2D g2 = buffered.createGraphics();
             g2.drawImage(resized, 0, 0, null);
             g2.dispose();

             ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ImageIO.write(buffered, "jpg", baos); // 또는 png
             return baos.toByteArray();
         } catch (Exception e) {
             e.printStackTrace();
             return null;
         }
     }
    // ===== 날짜 포맷터 =====
    private static class DateLabelFormatter extends JFormattedTextField.AbstractFormatter {
        private final String datePattern = "yyyy-MM-dd";
        private final SimpleDateFormat dateFormatter = new SimpleDateFormat(datePattern);

        @Override
        public Object stringToValue(String text) throws ParseException {
            Calendar cal = Calendar.getInstance();
            cal.setTime(dateFormatter.parse(text));
            return cal;
        }

        @Override
        public String valueToString(Object value) throws ParseException {
            if (value instanceof Calendar cal) {
                return dateFormatter.format(cal.getTime());
            }
            return "";
        }
    }

    // ===== 실행 진입점(테스트용) =====
    public static void main(String[] args) {
        SwingUtilities.invokeLater(JoinFrame::new);
    }
   }
