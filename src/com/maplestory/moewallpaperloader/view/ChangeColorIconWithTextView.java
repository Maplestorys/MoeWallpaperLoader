package com.maplestory.moewallpaperloader.view;

import com.maplestory.moewallpaperloader.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

public class ChangeColorIconWithTextView extends View
{

	private Bitmap mBitmap;
	private Canvas mCanvas;
	private Paint mPaint;
	/**
	 * ��ɫ
	 */
	private int mColor = 0xFF45C01A;
	/**
	 * ͸���� 0.0-1.0
	 */
	private float mAlpha = 0f;
	/**
	 * ͼ��
	 */
	private Bitmap mIconBitmap;
	/**
	 * ���ƻ���icon�ķ�Χ
	 */
	private Rect mIconRect;
	/**
	 * icon�ײ��ı�
	 */
	private String mText = "΢��";
	private int mTextSize = (int) TypedValue.applyDimension(
			TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics());
	private Paint mTextPaint;
	private Rect mTextBound = new Rect();

	public ChangeColorIconWithTextView(Context context)
	{
		super(context);
	}

	/**
	 * ��ʼ���Զ�������ֵ
	 * 
	 * @param context
	 * @param attrs
	 */
	public ChangeColorIconWithTextView(Context context, AttributeSet attrs)
	{
		super(context, attrs);

		// ��ȡ���õ�ͼ��
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.ChangeColorIconView);

		int n = a.getIndexCount();
		for (int i = 0; i < n; i++)
		{

			int attr = a.getIndex(i);
			switch (attr)
			{
			case R.styleable.ChangeColorIconView_icon:
				BitmapDrawable drawable = (BitmapDrawable) a.getDrawable(attr);
				mIconBitmap = drawable.getBitmap();
				break;
			case R.styleable.ChangeColorIconView_color:
				mColor = a.getColor(attr, 0x45C01A);
				break;
			case R.styleable.ChangeColorIconView_text:
				mText = a.getString(attr);
				break;
			case R.styleable.ChangeColorIconView_text_size:
				mTextSize = (int) a.getDimension(attr, TypedValue
						.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10,
								getResources().getDisplayMetrics()));
				break;

			}
		}

		a.recycle();

		mTextPaint = new Paint();
		mTextPaint.setTextSize(mTextSize);
		mTextPaint.setColor(0xff555555);
		// �õ�text���Ʒ�Χ
		mTextPaint.getTextBounds(mText, 0, mText.length(), mTextBound);

	}
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

//		int bitmapWidth = Math.min(getMeasuredWidth() - getPaddingLeft()
//				- getPaddingRight(), getMeasuredHeight() - getPaddingTop()
//				- getPaddingBottom() - mTextBound.height());
//
//		int left = getMeasuredWidth() / 2 - bitmapWidth / 2;
//		int top = (getMeasuredHeight() - mTextBound.height()) / 2 - bitmapWidth
//				/ 2;
//		// ����icon�Ļ��Ʒ�Χ
//		mIconRect = new Rect(left, top, left + bitmapWidth, top + bitmapWidth);
		
		
		
		// �õ�����icon�Ŀ�
		int bitmapWidth = Math.min(getMeasuredWidth() - getPaddingLeft()
				- getPaddingRight()- mTextBound.width(), getMeasuredHeight() - getPaddingTop()
				- getPaddingBottom() );

		int left = ((getMeasuredWidth() - mTextBound.width()) - bitmapWidth)/2;
		int top = (getMeasuredHeight() - bitmapWidth)/2;
		// ����icon�Ļ��Ʒ�Χ
		mIconRect = new Rect(left, top, left + bitmapWidth, top + bitmapWidth);

	}
	@Override
	protected void onDraw(Canvas canvas)
	{

		int alpha = (int) Math.ceil((255 * mAlpha));
		canvas.drawBitmap(mIconBitmap, null, mIconRect, null);
		setupTargetBitmap(alpha);
		drawSourceText(canvas, alpha);
		drawTargetText(canvas, alpha);
		canvas.drawBitmap(mBitmap, 0, 0, null);

	}
	
	private void setupTargetBitmap(int alpha)
	{
		mBitmap = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(),
				Config.ARGB_8888);
		mCanvas = new Canvas(mBitmap);
		mPaint = new Paint();
		mPaint.setColor(mColor);
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setAlpha(alpha);
		mCanvas.drawRect(mIconRect, mPaint);
		mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
		mPaint.setAlpha(255);
		mCanvas.drawBitmap(mIconBitmap, null, mIconRect, mPaint);
	}

	private void drawSourceText(Canvas canvas, int alpha)
	{
		mTextPaint.setTextSize(mTextSize);
		mTextPaint.setColor(0xff333333);
		mTextPaint.setAlpha(255 - alpha);
		canvas.drawText(mText, mIconRect.right
				,
				mIconRect.bottom, mTextPaint);
	}
	
	private void drawTargetText(Canvas canvas, int alpha)
	{
		mTextPaint.setColor(mColor);
		mTextPaint.setAlpha(alpha);
		canvas.drawText(mText, mIconRect.right
				,
				mIconRect.bottom, mTextPaint);
		
	}
	public void setIconAlpha(float alpha)
	{
		this.mAlpha = alpha;
		invalidateView();
	}

	private void invalidateView()
	{
		if (Looper.getMainLooper() == Looper.myLooper())
		{
			invalidate();
		} else
		{
			postInvalidate();
		}
	}

}
