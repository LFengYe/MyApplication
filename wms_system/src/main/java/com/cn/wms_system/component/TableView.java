package com.cn.wms_system.component;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.cn.wms_system.R;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TableView extends LinearLayout {

	public static Paint BLACK_PAINT = new Paint();
	public static Paint WHITE_PAINT = new Paint();
	
	/** 降序排序*/
	public static final int SORT_DESCENDING = 0;
	/** 升序排序*/
	public static final int SORT_ASCENDING = 1;
	
	static {
		WHITE_PAINT.setColor(Color.WHITE);
		BLACK_PAINT.setColor(Color.BLACK);
	}

	private CAdapter cAdapter;

	/** 标题控件. */
	private LinearLayout titleLayout;
	/** 标题数据 */
	private String[] title;

	/** 列表控件. */
	private ListView listView;
	/** 列表数据. */
	private List<String[]> data;

	/** 列宽数据. */
	private int[] itemWidth;
	private int itemHeight;

	/** 列表数据排序方式 */
	private int sortRule = SORT_DESCENDING;
	/** 当前选中行. */
	private int selectedPosition = -1;
	/** 自动列宽列. */
	private int autoWidthIndex = -1;

	/** 触摸事件模式 */
//	private int mode = 0;
	/** 上次两指距离 */
//	private float oldDist;
	/** 表格行点击事件 */
	private AdapterView.OnItemClickListener onItemClickListener;
	/** 行背景颜色. */
	private int[] rowsBackgroundColor;
	/** 选中行背景颜色. */
	private int selectedBackgroundColor = getResources().getColor(R.color.the_table_seleted_bg_color);
//	private int selectedBackgroundColor = Color.argb(200, 224, 243, 250);
	/** 标题背景颜色. */
	private int titleBackgroundColor;
	/** 标题字体颜色. */
	private int titleTextColor = Color.argb(255, 100, 100, 100);
	/** 内容字体颜色. */
	private int contentTextColor = Color.argb(255, 100, 100, 100);
	/** 标题字体大小. */
	private float titleTextSize = 0;
	/** 内容字体大小. */
	private float contentTextSize = 0;

	private ButtonClickInterFace btnClick;

	public ButtonClickInterFace getBtnClick() {
		return btnClick;
	}

	public void setBtnClick(ButtonClickInterFace btnClick) {
		this.btnClick = btnClick;
	}

	public TableView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public TableView(Context context) {
		super(context);
	}
	
	/**
	 * 初始化带标题ListView
	 * 
	 * @param context
	 *            父级上下文
	 * @param title
	 *            标题数组
	 * @param data
	 *            内容列表
	 */
	public TableView(Context context, String[] title, List<String[]> data, boolean canSelected) {
		super(context);

		this.title = title;
		this.data = data;
		// 设定纵向布局
		setOrientation(VERTICAL);
		// 设定背景为白色
		setBackgroundColor(Color.WHITE);

		// 预先设定好每列的宽
		this.itemWidth = new int[this.title.length];
		autoWidthIndex = this.title.length - 1;
		// 计算列宽
		calcColumnWidth();

		// 添加title位置
		titleLayout = new LinearLayout(getContext());
		titleLayout.setBackgroundColor(Color.parseColor("#CCCCCC"));
		addView(titleLayout, Constants.HORIZONTAL_FILL_LAYOUTPATAMS);
		// 绘制标题面板
		drawTitleLayout();

		// 添加listview
		listView = new ListView(getContext());
		listView.setPadding(0, 2, 0, 0);
		cAdapter = new CAdapter();
		cAdapter.setCanSelected(canSelected);
		listView.setAdapter(cAdapter);
		listView.setCacheColorHint(0);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (onItemClickListener != null)
					onItemClickListener.onItemClick(parent, view, position, id);
				setSelectedPosition(position);
				cAdapter.notifyDataSetChanged();
			}
		});
		addView(listView, Constants.FILL_FILL_LAYOUTPARAMS);
	}

	/**
	 * 设置数据视图始终滚动
	 */
	public void setDataScroll() {
		listView.setStackFromBottom(true);
		listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
	}
	
	/**
	 * 整体有改变时，刷新显示
	 */
	public void definedSetChanged() {
		calcColumnWidth();
		drawTitleLayout();
		cAdapter.notifyDataSetChanged();
	}

	/**
	 * 重新设置数据，刷新列表
	 */
	public void refreshData(List<String[]> data) {
		this.data = data;
		calcColumnWidth();
		drawTitleLayout();
		cAdapter.notifyDataSetChanged();
	}
	
	/**
	 * 设置选中时的监听器
	 * 
	 * @param onItemClickListener
	 */
	public void setOnItemClickListener(
			AdapterView.OnItemClickListener onItemClickListener) {
		this.onItemClickListener = onItemClickListener;
	}

	/**
	 * 设置表格内容指定行的背景颜色
	 * @param index
	 * @param color
	 */
	public void setSpecifiedItemBackgroundColor(int index, int color) {
		View v = listView.getChildAt(index);
		if (v != null)
			v.setBackgroundColor(color);
	}
	
	/**
	 * 设置行背景颜色, 多个颜色可以作为间隔色
	 * 
	 * @param color
	 *            行背景颜色，可以为多个
	 */
	public void setItemBackgroundColor(int... color) {
		rowsBackgroundColor = color;
	}

	/**
	 * 数据总数
	 */
	public int getCount() {
		if (data == null)
			return 0;
		return data.size();
	}

	/**
	 * 当前选中数据
	 * 
	 * @param position
	 * @return
	 */
	public String[] getItem(int position) {
		if (data == null)
			return null;
		return data.get(position);
	}

	/**
	 * 设置当前选中位置
	 * 
	 * @return
	 */
	public void setSelectedPosition(int selectedPosition) {
		this.selectedPosition = selectedPosition;
	}

	/**
	 * 当前选中位置
	 * 
	 * @return
	 */
	public int getSelectedPosition() {
		return selectedPosition;
	}

	/**
	 * 设置被选中时的背景色
	 * 
	 * @param color
	 */
	public void setSelectedBackgroundColor(int color) {
		selectedBackgroundColor = color;
	}

	/**
	 * 设置标题背景色.
	 * 
	 * @param color
	 */
	public void setTitleBackgroundColor(int color) {
		titleBackgroundColor = color;
		titleLayout.setBackgroundColor(titleBackgroundColor);
	}

	/**
	 * 设置标题文字颜色
	 * 
	 * @param color
	 */
	public void setTitleTextColor(int color) {
		titleTextColor = color;
		for (int i = 0; i < titleLayout.getChildCount(); i++) {
			((TextView) titleLayout.getChildAt(i)).setTextColor(titleTextColor);
		}
	}

	/**
	 * 设置内容文字颜色
	 * 
	 * @param color
	 */
	public void setContentTextColor(int color) {
		contentTextColor = color;
	}
	
	/**
	 * 设置标题字体大小
	 * 
	 * @param szie
	 */
	public void setTitleTextSize(float szie) {
		titleTextSize = szie;
	}

	/**
	 * 设置内容字体大小
	 * 
	 * @param szie
	 */
	public void setContentTextSize(float szie) {
		contentTextSize = szie;
	}
	
	/**
	 * 
	 * 设定哪列自动列宽 从0开始计算
	 * 
	 * @param index
	 */
	public void setAutoColumnWidth(int index) {
		autoWidthIndex = index;
		for (int i = 0; i < titleLayout.getChildCount(); i++) {
			TextView tv = ((TextView) titleLayout.getChildAt(i));
			if (i == autoWidthIndex)
				tv.setLayoutParams(Constants.HORIZONTAL_FILL_LAYOUTPATAMS);
			else {
				tv.setLayoutParams(Constants.WRAP_WRAP_LAYOUTPARAMS);
				tv.setWidth(itemWidth[i]);
			}
		}
	}

	/**
	 * 绘制标题
	 */
	private void drawTitleLayout() {
		
		titleLayout.removeAllViews();
		for (int i = 0; i < title.length; i++) {
			/**
			 * 文本控件初始化
			 */
			TextView tv = new CTextView(titleLayout.getContext());
			tv.setTextColor(titleTextColor);
			tv.setGravity(Gravity.CENTER);
			tv.setId(i);
			
			//设置文本控件宽度
			tv.setWidth(itemWidth[i]);
			//设置文本控件字体大小
			if (titleTextSize > 0) {
				tv.setTextSize(titleTextSize);
			}
			//设置文本控件文字
			tv.setText(title[i]);

			//设置标题中点击字段根据该字段对表格内容进行排序
			tv.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					/**
					 * 再此处根据点击字段，对数据进行排序
					 */
					data = orderData(data, v.getId());
					cAdapter.notifyDataSetChanged();
					//多次点击同一字段，升降排序交换
					if (sortRule == SORT_ASCENDING) {
						sortRule = SORT_DESCENDING;
						return ;
					}
					if (sortRule == SORT_DESCENDING) {
						sortRule = SORT_ASCENDING;
						return ;
					}
				}
			});
			
			//文本控件添加到表格标题
			if (i == autoWidthIndex)
				titleLayout.addView(tv, Constants.HORIZONTAL_FILL_LAYOUTPATAMS);
			else
				titleLayout.addView(tv, Constants.WRAP_WRAP_LAYOUTPARAMS);
		}
	}

	/**
	 * 根据制定字段对列表数据进行排序
	 * @param data
	 * @param filedIndex
	 * @return 排序结果
	 */
	public List<String[]> orderData(List<String[]> data, int filedIndex) {
		String[] temp;
		for (int i = 0; i < data.size(); i++) {
			for (int j = i; j < data.size() - 1; j++) {
				//升序排列
				if (sortRule == SORT_ASCENDING && 
						data.get(i)[filedIndex].compareTo(data.get(j + 1)[filedIndex]) > 0)
				{
					temp = data.get(i);
					data.set(i, data.get(j + 1));
					data.set(j + 1, temp);
				}
				
				//降序排列
				if (sortRule == SORT_DESCENDING &&
						data.get(i)[filedIndex].compareTo(data.get(j + 1)[filedIndex]) < 0)
				{
					temp = data.get(i);
					data.set(i, data.get(j + 1));
					data.set(j + 1, temp);
				}
			}
		}
		Collections.sort(data, comparator);
		return data;
	}

	Comparator<String[]> comparator = new Comparator<String[]>() {
		
		@Override
		public int compare(String[] lhs, String[] rhs) {
			return lhs[lhs.length - 1].compareTo(rhs[rhs.length - 1]);
		}
	};

	public int getItemHeight() {
		return itemHeight;
	}

	public void setItemHeight(int itemHeight) {
		this.itemHeight = itemHeight;
	}

	/**
	 * @return the sortRule
	 */
	public int getSortRule() {
		return sortRule;
	}

	/**
	 * @param sortRule the sortRule to set
	 */
	public void setSortRule(int sortRule) {
		this.sortRule = sortRule;
	}

	/**
	 * 计算列宽
	 * 
	 * @return 是否有改动
	 */
	private boolean calcColumnWidth() {
		boolean result = false;

		/**
		 * 方案一：
		 * float textSize = new TextView(getContext()).getTextSize();
		 */
		
		/**
		 * 方案二：
		 * DisplayMetrics metrics = getResources().getDisplayMetrics();
		 * float value = metrics.scaledDensity;
		 * float textSize = new TextView(getContext()).getTextSize() / value;
		 * 此时所得字体大小才是真实字体大小
		 */
		float textSize = new TextView(getContext()).getTextSize();
		
		// 计算标题列宽
		for (int i = 0; i < itemWidth.length; i++) {
			int w = (int) getPixelByText((titleTextSize > 0) ? titleTextSize
					: textSize, title[i]);
			itemWidth[i] = w;
//			if (itemWidth[i] < w) {
//				itemWidth[i] = w;
//				result = true;
//			}
		}

		if (contentTextSize > 0) {
			textSize = contentTextSize;
		}
		// 计算内容列宽
		for (int i = 0; i < data.size(); i++) {
			for (int j = 0; j < itemWidth.length && j < data.get(i).length; j++) {
				int w = (int) getPixelByText(textSize, data.get(i)[j]);
				if (itemWidth[j] < w) {
					itemWidth[j] = w;
					result = true;
				}
			}
		}
		return result;
	}

	/**
	 * 计算字符串所占像素
	 * 
	 * @param textSize
	 *            字体大小
	 * @param text
	 *            字符串
	 * @return 字符串所占像素
	 */
	private int getPixelByText(float textSize, String text) {
		
		/**
		 * 计算像素点时，需考虑到屏幕像素点密度
		 * 即再字体大小上乘以密度，否则再屏幕像素点不为1时，绘制出的表格会出现混乱
		 */
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		float value = metrics.density;
		
		Paint mTextPaint = new Paint();
		mTextPaint.setTextSize(textSize * value); // 指定字体大小
		mTextPaint.setFakeBoldText(true); // 粗体
		mTextPaint.setAntiAlias(true); // 非锯齿效果

		return (int) (mTextPaint.measureText(text) + textSize);
	}

	/**
	 * 主要用的Adapter
	 */
	class CAdapter extends BaseAdapter {

		private boolean canSelected;

		@Override
		public int getCount() {
			if (data == null)
				return 0;
			return data.size();
		}

		@Override
		public Object getItem(int position) {
			if (data == null)
				return null;
			return data.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		/**
		 * @return the canSelected
		 */
		public boolean isCanSelected() {
			return canSelected;
		}

		/**
		 * @param canSelected the canSelected to set
		 */
		public void setCanSelected(boolean canSelected) {
			this.canSelected = canSelected;
		}


		/**
		 * 生成表格的一行
		 */

		@SuppressLint("NewApi")
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {

			// 初始化主layout
			LinearLayout contextLayout = new LinearLayout(
					TableView.this.getContext());

			String[] dataItem = data.get(position);

			//设置表格行的背景颜色
			if (getSelectedPosition() == position) { // 为当前选中行
				contextLayout.setBackgroundColor(selectedBackgroundColor);
			} else if (rowsBackgroundColor != null
					&& rowsBackgroundColor.length > 0) {
				contextLayout.setBackgroundColor(rowsBackgroundColor[position
						% rowsBackgroundColor.length]);
			}

			for (int i = 0; i < title.length; i++) {

				if (title[i].compareTo("操作") == 0 && isCanSelected()) {
					TextView button = new CTextView(contextLayout.getContext());
					button.setGravity(Gravity.CENTER);
					button.setLayoutParams(Constants.WRAP_WRAP_LAYOUTPARAMS);
					button.setBackgroundResource(R.drawable.button_shape);
					button.setFocusable(false);
					button.setClickable(false);
					button.setTextColor(Color.BLACK);
					if (Integer.valueOf(dataItem[i]) == 1) {
						button.setText("开始");
					}
					if (Integer.valueOf(dataItem[i]) == 2) {
						button.setText("完成");
					}
					button.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							btnClick.btnClick(position);
						}
					});
					contextLayout.addView(button, Constants.FILL_FILL_LAYOUTPARAMS);
					continue;
				}
				/*
				/**
				 * 文本控件初始化
				 */
				TextView tv = new CTextView(contextLayout.getContext());
//				tv.setBackgroundResource(R.drawable.test_shape);
				tv.setTextColor(contentTextColor);
				tv.setGravity(Gravity.CENTER);

				/*
				if (dataItem[0].compareTo("紧急计划") == 0)//紧急计划－－－特殊
					tv.setTextColor(getResources().getColor(R.color.the_table_urgent_text_color));

				if (dataItem[dataItem.length - 1].compareTo("1") == 0)//已完成明细字体颜色
					tv.setTextColor(getResources().getColor(R.color.the_detail_finished_text_color));
					if (title[i].compareTo("备注") == 0)
					tv.setGravity(Gravity.LEFT);
				*/


				//设置文本控件宽度
				if (i == autoWidthIndex)
					tv.setLayoutParams(Constants.FILL_FILL_LAYOUTPARAMS);
				else {
					tv.setWidth(itemWidth[i]);
				}

				//设置文本控件字体大小
				if (contentTextSize > 0) {
					tv.setTextSize(contentTextSize);
				}
				//设置文本控件文字
				if (i < dataItem.length) {
					tv.setText(dataItem[i]);
				}
				//将文本控件添加到表格中
				if (i == autoWidthIndex)
					contextLayout.addView(tv, Constants.FILL_FILL_LAYOUTPARAMS);
				else
					contextLayout.addView(tv);
			}

			return contextLayout;
		}


		/**
		 * 根据输入的整数，将该整数生成三位数的序号，不足三位的在前面添0
		 * @param position
		 * @return
		 */
		public String createSerialNumber(int position) {
			String result = String.valueOf(position);
			if (result.length() <= 3)
				result = "0" + result;
			return result;
		}
	}

	/**
	 * 重写的TextView
	 */
	class CTextView extends TextView {

		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);
			// Top
			canvas.drawLine(0, 0, this.getWidth() - 1, 0, WHITE_PAINT);
			// Left
			canvas.drawLine(0, 0, 0, this.getHeight() - 1, WHITE_PAINT);
			// Right
			canvas.drawLine(this.getWidth() - 1, 0, this.getWidth() - 1,
					this.getHeight() - 1, BLACK_PAINT);
			// Buttom
			canvas.drawLine(0, this.getHeight() - 1, this.getWidth() - 1,
					this.getHeight() - 1, BLACK_PAINT);
		}

		public CTextView(Context context) {
			super(context);
		}
	}

	class MyButton extends Button {
		public MyButton(Context context) {
			super(context);
		}

		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);
			// Top
			canvas.drawLine(0, 0, this.getWidth() - 1, 0, WHITE_PAINT);
			// Left
			canvas.drawLine(0, 0, 0, this.getHeight() - 1, WHITE_PAINT);
			// Right
			canvas.drawLine(this.getWidth() - 1, 0, this.getWidth() - 1,
					this.getHeight() - 1, BLACK_PAINT);
			// Buttom
			canvas.drawLine(0, this.getHeight() - 1, this.getWidth() - 1,
					this.getHeight() - 1, BLACK_PAINT);
		}
	}

	class MyCheckBox extends CheckBox {

		public MyCheckBox(Context context) {
			super(context);
		}

		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);

			// Top
			canvas.drawLine(0, 0, this.getWidth() - 1, 0, WHITE_PAINT);
			// Left
			canvas.drawLine(0, 0, 0, this.getHeight() - 1, WHITE_PAINT);
			// Right
			canvas.drawLine(this.getWidth() - 1, 0, this.getWidth() - 1,
					this.getHeight() - 1, BLACK_PAINT);
			// Buttom
			canvas.drawLine(0, this.getHeight() - 1, this.getWidth() - 1,
					this.getHeight() - 1, BLACK_PAINT);
		}
	}

	public interface ButtonClickInterFace {
		void btnClick(int position);
	}
}
