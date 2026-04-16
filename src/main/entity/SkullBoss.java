package main.entity;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import main.GamePanel;

/**
 * Stage 3 Boss (SkullBoss) — บอสด่านที่ 3 (บอสสุดท้าย)
 * * โหดกว่า KekeBoss: เลือดเยอะกว่า (200 HP) และความเร็วพื้นฐานสูงกว่า
 * * มีระบบ "สุ่มความเร็วโจมตี" และ "คอมโบรัวหมัด (Fight Loop)"
 */
public class SkullBoss extends PlayerValue {

    // ── สถานะบอส ───────────────────────────────────────────────────
    public static final int STATE_IDLE       = 0;
    public static final int STATE_ATTACK     = 1;
    public static final int STATE_DEATH      = 2;
    public static final int STATE_FIGHT_LOOP = 3; // ท่าโจมตีแบบต่อเนื่อง (Combo)

    GamePanel gp;
    public int state;
    int frameIndex;
    int animationCounter;
    int frameDelay;
    int loopCount = 0; // ตัวนับจำนวนรอบของท่า Fight Loop

    // ความเร็วพื้นฐาน (น้อย = เร็ว) KekeBoss คือ 8 ดังนั้น SkullBoss ให้เร็วกว่านิดนึง
    final int BASE_FRAME_DELAY = 7;

    // ── อาร์เรย์รูปภาพ ─────────────────────────────────────────────────
    public BufferedImage[] idleImages;
    public BufferedImage[] attackImages;
    public BufferedImage[] fightLoopImages;
    public BufferedImage[] deathImages;

    // ── สถิติบอส ──────────────────────────────────────────────────────
    public int maxHp;
    public int hp;
    public boolean isDead;

    public SkullBoss(GamePanel gp) {
        this.gp = gp;

        // ตำแหน่งบอสบนหน้าจอ
        this.x = (gp.screenWidth / 2) - 150;
        this.y = 50;

        state            = STATE_IDLE;
        frameIndex       = 0;
        animationCounter = 0;
        frameDelay       = BASE_FRAME_DELAY;

        // บอสด่าน 3 HP เยอะที่สุด
        maxHp  = 200;
        hp     = maxHp;
        isDead = false;

        getBossImage();
    }

    public void getBossImage() {
        try {
            // --- Idle (เฟรม 4-10 รวม 7 รูป) ---
            idleImages = new BufferedImage[7];
            for (int i = 0; i < 7; i++) {
                idleImages[i] = ImageIO.read(getClass().getResourceAsStream(
                        "/main/skullboss/SkullBoss" + (i + 4) + ".png"));
            }

            // --- Fight/Attack (เฟรม 12-26 รวม 15 รูป) ---
            attackImages = new BufferedImage[15];
            for (int i = 0; i < 15; i++) {
                attackImages[i] = ImageIO.read(getClass().getResourceAsStream(
                        "/main/skullboss/SkullBoss" + (i + 12) + ".png"));
            }

            // --- Fight Loop (เฟรม 28-31 รวม 4 รูป) ---
            fightLoopImages = new BufferedImage[4];
            for (int i = 0; i < 4; i++) {
                fightLoopImages[i] = ImageIO.read(getClass().getResourceAsStream(
                        "/main/skullboss/SkullBoss" + (i + 28) + ".png"));
            }

            // --- Death (เฟรม 34-47 รวม 14 รูป) ---
            deathImages = new BufferedImage[14];
            for (int i = 0; i < 14; i++) {
                deathImages[i] = ImageIO.read(getClass().getResourceAsStream(
                        "/main/skullboss/SkullBoss" + (i + 34) + ".png"));
            }

        } catch (Exception e) {
            System.err.println("[SkullBoss] โหลดรูปไม่ได้: " + e.getMessage());
        }
    }

    public void triggerAttack() {
        if (!isDead && state == STATE_IDLE) {
            // สุ่มรูปแบบการโจมตี 3 แบบให้เด็กๆ คาดเดายากขึ้น
            int randomMove = (int) (Math.random() * 3);

            if (randomMove == 0) {
                // แบบที่ 1: โจมตีปกติแต่เร็วกว่าท่ายืน
                state = STATE_ATTACK;
                frameDelay = 5;
            } else if (randomMove == 1) {
                // แบบที่ 2: ใช้ท่ารัวหมัด (Fight Loop) จะวนลูป 3 รอบ
                state = STATE_FIGHT_LOOP;
                frameDelay = 5;
                loopCount = 0; // เริ่มนับรอบคอมโบใหม่
            } else {
                // แบบที่ 3: โจมตีด้วยความเร็วสูงมาก (Surprise attack!)
                state = STATE_ATTACK;
                frameDelay = 3;
            }

            frameIndex       = 0;
            animationCounter = 0;
        }
    }

    public void update() {
        if (isDead) return;

        if (hp <= 0 && state != STATE_DEATH) {
            state            = STATE_DEATH;
            frameDelay       = BASE_FRAME_DELAY; // ตอนตายให้ความเร็วปกติ
            frameIndex       = 0;
            animationCounter = 0;
            return;
        }

        animationCounter++;
        if (animationCounter >= frameDelay) {
            animationCounter = 0;

            if (state == STATE_IDLE) {
                frameIndex++;
                if (idleImages != null && frameIndex >= idleImages.length) {
                    frameIndex = 0;
                }
            } else if (state == STATE_ATTACK) {
                frameIndex++;
                if (attackImages != null && frameIndex >= attackImages.length) {
                    resetToIdle();
                }
            } else if (state == STATE_FIGHT_LOOP) {
                frameIndex++;
                // ท่าคอมโบ เมื่อเล่นจบ 4 เฟรม ให้นับรอบ
                if (fightLoopImages != null && frameIndex >= fightLoopImages.length) {
                    loopCount++;
                    if (loopCount >= 3) { // วน 3 รอบค่อยกลับไปยืนพัก
                        resetToIdle();
                    } else {
                        frameIndex = 0; // วนลูปกลับไปเฟรมแรกของท่านี้ใหม่
                    }
                }
            } else if (state == STATE_DEATH) {
                frameIndex++;
                if (deathImages != null && frameIndex >= deathImages.length) {
                    frameIndex = deathImages.length - 1; // ล้มแล้วค้างที่เฟรมสุดท้าย
                    isDead     = true;
                }
            }
        }
    }

    // ฟังก์ชันช่วยสำหรับกลับไปท่ายืนปกติและคืนค่าความเร็ว
    private void resetToIdle() {
        state      = STATE_IDLE;
        frameIndex = 0;
        frameDelay = BASE_FRAME_DELAY;
    }

    public void draw(Graphics2D g2) {
        BufferedImage imageToDraw = null;

        switch (state) {
            case STATE_IDLE:
                if (idleImages != null && frameIndex < idleImages.length) imageToDraw = idleImages[frameIndex];
                break;
            case STATE_ATTACK:
                if (attackImages != null && frameIndex < attackImages.length) imageToDraw = attackImages[frameIndex];
                break;
            case STATE_FIGHT_LOOP:
                if (fightLoopImages != null && frameIndex < fightLoopImages.length) imageToDraw = fightLoopImages[frameIndex];
                break;
            case STATE_DEATH:
                if (deathImages != null && frameIndex < deathImages.length) imageToDraw = deathImages[frameIndex];
                break;
        }

        if (imageToDraw != null) {
            int originalWidth  = imageToDraw.getWidth();
            int originalHeight = imageToDraw.getHeight();
            // ปรับขนาดวาดบอสให้สวยงาม (สามารถปรับเลขได้ตามต้องการ)
            int desiredWidth   = 300;
            int desiredHeight  = 200;

            double scale       = Math.min((double) desiredWidth / originalWidth,
                    (double) desiredHeight / originalHeight);
            int scaledWidth    = (int)(originalWidth  * scale);
            int scaledHeight   = (int)(originalHeight * scale);
            int drawX          = this.x + (desiredWidth  - scaledWidth)  / 2;
            int drawY          = this.y + (desiredHeight - scaledHeight) / 2;

            g2.drawImage(imageToDraw, drawX, drawY, scaledWidth, scaledHeight, null);

        } else if (state == STATE_IDLE) {
            // กรณีโหลดรูปไม่ติด
            g2.setColor(new Color(150, 0, 200));
            g2.setFont(new Font("Arial", Font.BOLD, 28));
            g2.drawString("[ SKULL BOSS ]", this.x + 20, this.y + 100);
        }
    }
}