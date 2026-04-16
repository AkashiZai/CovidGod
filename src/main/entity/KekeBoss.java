package main.entity;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import main.GamePanel;

/**
 * Stage3Boss — บอสด่านที่ 3 (บอสสุดท้าย / ยากที่สุด)
 *
 * วิธีใส่รูปบอส:
 *   1. สร้างโฟลเดอร์ใหม่ชื่อ "stage3boss" ใน src/main/ (เหมือน scytheboss/)
 *   2. ใส่รูป PNG ลงในโฟลเดอร์นั้น
 *   3. แก้ชื่อไฟล์ใน getBossImage() ด้านล่างตามชื่อจริงของรูปคุณ
 *
 * โครงสร้างรูป (ปรับได้ตามจำนวนเฟรมจริง):
 *   Idle   → Stage3Boss_Idle1.png   ~ Stage3Boss_Idle7.png    (7 รูป)
 *   Attack → Stage3Boss_Attack1.png ~ Stage3Boss_Attack9.png  (9 รูป)
 *   Death  → Stage3Boss_Death1.png  ~ Stage3Boss_Death16.png  (16 รูป)
 */
public class KekeBoss extends PlayerValue {

    // ── สถานะบอส (เหมือน ScytheBoss) ─────────────────────────────────
    public static final int STATE_IDLE   = 0;
    public static final int STATE_ATTACK = 1;
    public static final int STATE_DEATH  = 2;

    GamePanel gp;
    public int state;
    int frameIndex;
    int animationCounter;
    int frameDelay;

    // ── อาร์เรย์รูปภาพ ─────────────────────────────────────────────────
    public BufferedImage[] idleImages;
    public BufferedImage[] attackImages;
    public BufferedImage[] deathImages;

    // ── สถิติบอส ──────────────────────────────────────────────────────
    public int maxHp;
    public int hp;
    public boolean isDead;

    public KekeBoss(GamePanel gp) {
        this.gp = gp;

        // ── ตำแหน่งบอสบนหน้าจอ ───────────────────────────────────────
        this.x = (gp.screenWidth / 2) - 150;
        this.y = 50;

        state            = STATE_IDLE;
        frameIndex       = 0;
        animationCounter = 0;
        frameDelay       = 10;

        // บอสด่าน 3 มี HP มากที่สุด (200 HP)
        maxHp  = 200;
        hp     = maxHp;
        isDead = false;

        getBossImage();
    }

    public void getBossImage() {
        try {
            // ============================================================
            // TODO: แปะรูปบอสด่าน 3 ตรงนี้
            //
            // ขั้นตอน:
            //   1. สร้างโฟลเดอร์ main/stage3boss/
            //   2. ใส่รูป PNG ของคุณลงไป
            //   3. เปลี่ยน path "/main/stage3boss/Stage3Boss_IdleX.png"
            //      ให้ตรงกับชื่อไฟล์จริง
            //
            // ตัวอย่างถ้ารูปชื่อ S3B1.png, S3B2.png, ...:
            //   idleImages[i] = ImageIO.read(getClass().getResourceAsStream(
            //       "/main/stage3boss/S3B" + (i+1) + ".png"));
            // ============================================================

            // --- Idle (7 เฟรม) ---
            idleImages = new BufferedImage[7];
            for (int i = 0; i < 7; i++) {
                idleImages[i] = ImageIO.read(getClass().getResourceAsStream(
                    "/main/stage3boss/Stage3Boss_Idle" + (i + 1) + ".png"));
            }

            // --- Attack (9 เฟรม) ---
            attackImages = new BufferedImage[9];
            for (int i = 0; i < 9; i++) {
                attackImages[i] = ImageIO.read(getClass().getResourceAsStream(
                    "/main/stage3boss/Stage3Boss_Attack" + (i + 1) + ".png"));
            }

            // --- Death (16 เฟรม) ---
            deathImages = new BufferedImage[16];
            for (int i = 0; i < 16; i++) {
                deathImages[i] = ImageIO.read(getClass().getResourceAsStream(
                    "/main/stage3boss/Stage3Boss_Death" + (i + 1) + ".png"));
            }

        } catch (Exception e) {
            System.err.println("[Stage3Boss] โหลดรูปไม่ได้: " + e.getMessage());
            idleImages   = null;
            attackImages = null;
            deathImages  = null;
        }
    }

    public void triggerAttack() {
        if (!isDead && state == STATE_IDLE) {
            state            = STATE_ATTACK;
            frameIndex       = 0;
            animationCounter = 0;
        }
    }

    public void update() {
        if (isDead) return;

        if (hp <= 0 && state != STATE_DEATH) {
            state            = STATE_DEATH;
            frameIndex       = 0;
            animationCounter = 0;
            return;
        }

        animationCounter++;
        if (animationCounter >= frameDelay) {
            animationCounter = 0;

            if (state == STATE_IDLE) {
                frameIndex++;
                if (idleImages != null && frameIndex >= idleImages.length)
                    frameIndex = 0;

            } else if (state == STATE_ATTACK) {
                frameIndex++;
                if (attackImages != null && frameIndex >= attackImages.length) {
                    state      = STATE_IDLE;
                    frameIndex = 0;
                }

            } else if (state == STATE_DEATH) {
                frameIndex++;
                if (deathImages != null && frameIndex >= deathImages.length) {
                    frameIndex = deathImages.length - 1;
                    isDead     = true;
                }
            }
        }
    }

    public void draw(Graphics2D g2) {
        BufferedImage imageToDraw = null;

        switch (state) {
            case STATE_IDLE:
                if (idleImages != null && frameIndex < idleImages.length)
                    imageToDraw = idleImages[frameIndex];
                break;
            case STATE_ATTACK:
                if (attackImages != null && frameIndex < attackImages.length)
                    imageToDraw = attackImages[frameIndex];
                break;
            case STATE_DEATH:
                if (deathImages != null && frameIndex < deathImages.length)
                    imageToDraw = deathImages[frameIndex];
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

        } else {
            // ── Placeholder เมื่อยังไม่มีรูป ──────────────────────────
            // TODO: ลบหรือ comment ส่วนนี้ออกหลังจากใส่รูปแล้ว
            g2.setColor(new Color(255, 60, 0));
            g2.setFont(new Font("Arial", Font.BOLD, 28));
            g2.drawString("[ STAGE 3 BOSS ]", this.x + 10, this.y + 100);
            g2.setFont(new Font("Arial", Font.PLAIN, 18));
            g2.setColor(Color.WHITE);
            g2.drawString("(วางรูปใน main/stage3boss/)", this.x, this.y + 130);
        }
    }
}
