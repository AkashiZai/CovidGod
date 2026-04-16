package main.entity;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import main.GamePanel;

/**
 * Stage 2 Boss (KekeBoss) — บอสด่านที่ 2
 * * เร็วกว่าและคาดเดายากกว่าบอสตัวแรก (ScytheBoss)
 * สุ่มรูปแบบการโจมตี/เดิน เพื่อเพิ่มความท้าทายแต่ไม่ยากเกินไปสำหรับเด็ก
 */
public class KekeBoss extends PlayerValue {

    // ── สถานะบอส ───────────────────────────────────────────────────
    public static final int STATE_IDLE    = 0;
    public static final int STATE_ATTACK  = 1;
    public static final int STATE_DEATH   = 2;
    public static final int STATE_WALK    = 3;
    public static final int STATE_SPECIAL = 4; // สำหรับอนิเมชัน "Something else"

    GamePanel gp;
    public int state;
    int frameIndex;
    int animationCounter;
    int frameDelay;

    // ความเร็วพื้นฐาน (น้อย = เร็ว) บอสตัวแรกคือ 10
    final int BASE_FRAME_DELAY = 8;

    // ── อาร์เรย์รูปภาพ ─────────────────────────────────────────────────
    public BufferedImage[] idleImages;
    public BufferedImage[] walkImages;
    public BufferedImage[] specialImages;
    public BufferedImage[] attackImages;
    public BufferedImage[] deathImages;

    // ── สถิติบอส ──────────────────────────────────────────────────────
    public int maxHp;
    public int hp;
    public boolean isDead;

    public KekeBoss(GamePanel gp) {
        this.gp = gp;

        // ตำแหน่งบอสบนหน้าจอ
        this.x = (gp.screenWidth / 2) - 150;
        this.y = 50;

        state            = STATE_IDLE;
        frameIndex       = 0;
        animationCounter = 0;
        frameDelay       = BASE_FRAME_DELAY;

        // บอสด่าน 2 HP เยอะกว่าด่านแรกนิดหน่อย (ด่านแรก 100)
        maxHp  = 150;
        hp     = maxHp;
        isDead = false;

        getBossImage();
    }

    public void getBossImage() {
        try {
            // --- Idle (เฟรม 1-6 รวม 6 รูป) ---
            idleImages = new BufferedImage[6];
            for (int i = 0; i < 6; i++) {
                idleImages[i] = ImageIO.read(getClass().getResourceAsStream(
                        "/main/kekeboss/ZKekeBoss" + (i + 1) + ".png"));
            }

            // --- Walk (เฟรม 8-19 รวม 12 รูป) ---
            walkImages = new BufferedImage[12];
            for (int i = 0; i < 12; i++) {
                walkImages[i] = ImageIO.read(getClass().getResourceAsStream(
                        "/main/kekeboss/ZKekeBoss" + (i + 8) + ".png"));
            }

            // --- Special/Something else (เฟรม 21-28 รวม 8 รูป) ---
            specialImages = new BufferedImage[8];
            for (int i = 0; i < 8; i++) {
                specialImages[i] = ImageIO.read(getClass().getResourceAsStream(
                        "/main/kekeboss/ZKekeBoss" + (i + 21) + ".png"));
            }

            // --- Attack (เฟรม 30-35 รวม 6 รูป) ---
            attackImages = new BufferedImage[6];
            for (int i = 0; i < 6; i++) {
                attackImages[i] = ImageIO.read(getClass().getResourceAsStream(
                        "/main/kekeboss/ZKekeBoss" + (i + 30) + ".png"));
            }

            // --- Death (เฟรม 37-42 รวม 6 รูป) ---
            deathImages = new BufferedImage[6];
            for (int i = 0; i < 6; i++) {
                deathImages[i] = ImageIO.read(getClass().getResourceAsStream(
                        "/main/kekeboss/ZKekeBoss" + (i + 37) + ".png"));
            }

        } catch (Exception e) {
            System.err.println("[KekeBoss] โหลดรูปไม่ได้: " + e.getMessage());
        }
    }

    public void triggerAttack() {
        if (!isDead && state == STATE_IDLE) {
            // ระบบสุ่มท่าเพื่อความคาดเดายาก (Unpredictability)
            int randomMove = (int) (Math.random() * 3); // สุ่มเลข 0, 1, 2

            if (randomMove == 0) {
                state = STATE_ATTACK;
                frameDelay = 5; // โจมตีเร็วมาก (เร็วกว่าปกติ)
            } else if (randomMove == 1) {
                state = STATE_SPECIAL;
                frameDelay = BASE_FRAME_DELAY; // ท่าพิเศษความเร็วปกติ
            } else {
                state = STATE_WALK;
                frameDelay = 7; // เดินขู่ด้วยความเร็วค่อนข้างไว
            }

            frameIndex       = 0;
            animationCounter = 0;
        }
    }

    public void update() {
        if (isDead) return;

        if (hp <= 0 && state != STATE_DEATH) {
            state            = STATE_DEATH;
            frameDelay       = BASE_FRAME_DELAY; // ปรับให้ความเร็วตอนตายเป็นปกติ
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
            } else if (state == STATE_WALK) {
                frameIndex++;
                if (walkImages != null && frameIndex >= walkImages.length) {
                    resetToIdle();
                }
            } else if (state == STATE_SPECIAL) {
                frameIndex++;
                if (specialImages != null && frameIndex >= specialImages.length) {
                    resetToIdle();
                }
            } else if (state == STATE_ATTACK) {
                frameIndex++;
                if (attackImages != null && frameIndex >= attackImages.length) {
                    resetToIdle();
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
            case STATE_WALK:
                if (walkImages != null && frameIndex < walkImages.length) imageToDraw = walkImages[frameIndex];
                break;
            case STATE_SPECIAL:
                if (specialImages != null && frameIndex < specialImages.length) imageToDraw = specialImages[frameIndex];
                break;
            case STATE_ATTACK:
                if (attackImages != null && frameIndex < attackImages.length) imageToDraw = attackImages[frameIndex];
                break;
            case STATE_DEATH:
                if (deathImages != null && frameIndex < deathImages.length) imageToDraw = deathImages[frameIndex];
                break;
        }

        if (imageToDraw != null) {
            int originalWidth  = imageToDraw.getWidth();
            int originalHeight = imageToDraw.getHeight();
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
            g2.setColor(new Color(255, 60, 0));
            g2.setFont(new Font("Arial", Font.BOLD, 28));
            g2.drawString("[ KEKE BOSS ]", this.x + 20, this.y + 100);
        }
    }
}