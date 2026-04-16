package main.entity;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import main.GamePanel;

/**
 * Stage2Boss — บอสด่านที่ 2
 *
 * วิธีใส่รูปบอส:
 *   1. สร้างโฟลเดอร์ใหม่ชื่อ "stage2boss" ใน src/main/ (เหมือน scytheboss/)
 *   2. ใส่รูป PNG ลงในโฟลเดอร์นั้น
 *   3. แก้ชื่อไฟล์ใน getBossImage() ด้านล่างตามชื่อจริงของรูปคุณ
 *
 * โครงสร้างรูป (ปรับได้ตามจำนวนเฟรมจริง):
 *   Idle   → Stage2Boss_Idle1.png   ~ Stage2Boss_Idle7.png    (7 รูป)
 *   Attack → Stage2Boss_Attack1.png ~ Stage2Boss_Attack9.png  (9 รูป)
 *   Death  → Stage2Boss_Death1.png  ~ Stage2Boss_Death16.png  (16 รูป)
 */
public class SkullBoss extends PlayerValue {

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

    public SkullBoss(GamePanel gp) {
        this.gp = gp;

        // ── ตำแหน่งบอสบนหน้าจอ ───────────────────────────────────────
        this.x = (gp.screenWidth / 2) - 150;
        this.y = 50;

        state            = STATE_IDLE;
        frameIndex       = 0;
        animationCounter = 0;
        frameDelay       = 10;

        // บอสด่าน 2 มี HP มากกว่าด่าน 1 (150 HP)
        maxHp  = 150;
        hp     = maxHp;
        isDead = false;

        getBossImage();
    }

    public void getBossImage() {
        try {
            // ============================================================
            // TODO: แปะรูปบอสด่าน 2 ตรงนี้
            //
            // ขั้นตอน:
            //   1. สร้างโฟลเดอร์ main/stage2boss/
            //   2. ใส่รูป PNG ของคุณลงไป
            //   3. เปลี่ยน path "/main/stage2boss/Stage2Boss_IdleX.png"
            //      ให้ตรงกับชื่อไฟล์จริง
            //
            // ตัวอย่างถ้ารูปชื่อ S2B1.png, S2B2.png, ...:
            //   idleImages[i] = ImageIO.read(getClass().getResourceAsStream(
            //       "/main/stage2boss/S2B" + (i+1) + ".png"));
            // ============================================================

            // --- Idle (7 เฟรม) ---
            // แก้ชื่อไฟล์ให้ตรงกับของคุณ
            idleImages = new BufferedImage[7];
            for (int i = 0; i < 7; i++) {
                idleImages[i] = ImageIO.read(getClass().getResourceAsStream(
                    "/main/stage2boss/Stage2Boss_Idle" + (i + 1) + ".png"));
            }

            // --- Attack (9 เฟรม) ---
            // แก้ชื่อไฟล์ให้ตรงกับของคุณ
            attackImages = new BufferedImage[9];
            for (int i = 0; i < 9; i++) {
                attackImages[i] = ImageIO.read(getClass().getResourceAsStream(
                    "/main/stage2boss/Stage2Boss_Attack" + (i + 1) + ".png"));
            }

            // --- Death (16 เฟรม) ---
            // แก้ชื่อไฟล์ให้ตรงกับของคุณ
            deathImages = new BufferedImage[16];
            for (int i = 0; i < 16; i++) {
                deathImages[i] = ImageIO.read(getClass().getResourceAsStream(
                    "/main/stage2boss/Stage2Boss_Death" + (i + 1) + ".png"));
            }

        } catch (Exception e) {
            // ถ้าโหลดรูปไม่ได้ จะ fallback ไปวาด placeholder แทน
            System.err.println("[Stage2Boss] โหลดรูปไม่ได้: " + e.getMessage());
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
                    frameIndex = deathImages.length - 1; // ค้างเฟรมสุดท้าย
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
            g2.setColor(new Color(0, 180, 255));
            g2.setFont(new Font("Arial", Font.BOLD, 28));
            g2.drawString("[ STAGE 2 BOSS ]", this.x + 10, this.y + 100);
            g2.setFont(new Font("Arial", Font.PLAIN, 18));
            g2.setColor(Color.WHITE);
            g2.drawString("(วางรูปใน main/stage2boss/)", this.x, this.y + 130);
        }
    }
}
