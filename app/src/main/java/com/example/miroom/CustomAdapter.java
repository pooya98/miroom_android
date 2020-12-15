package com.example.miroom;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class CustomAdapter extends BaseAdapter {

    // 문자열을 보관 할 ArrayList
    private ArrayList<RoomDTO> m_List;

    // 생성자
    public CustomAdapter() {
        m_List = new ArrayList<RoomDTO>();
    }

    // 현재 아이템의 수를 리턴
    @Override
    public int getCount() {
        return m_List.size();
    }

    // 현재 아이템의 오브젝트를 리턴, Object를 상황에 맞게 변경하거나 리턴받은 오브젝트를 캐스팅해서 사용
    @Override
    public Object getItem(int position) {
        return m_List.get(position);
    }

    // 아이템 position의 ID 값 리턴
    @Override
    public long getItemId(int position) {
        return position;
    }

    // 출력 될 아이템 관리
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        // 리스트가 길어지면서 현재 화면에 보이지 않는 아이템은 converView가 null인 상태로 들어 옴
        if ( convertView == null ) {
            // view가 null일 경우 커스텀 레이아웃을 얻어 옴
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item, parent, false);

            // TextView에 현재 position의 문자열 추가
            TextView text = (TextView) convertView.findViewById(R.id.name);
            text.setText(m_List.get(position).getRoom_name());

            TextView text2 = (TextView) convertView.findViewById(R.id.check_flag);
            text2.setText(m_List.get(position).getCheck_flag());


            // 리스트 아이템을 터치 했을 때 이벤트 발생
            convertView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // 터치 시 해당 아이템 이름 출력

                    Intent intent = new Intent(context, Book_page.class);
                    intent.putExtra("room_name", m_List.get(pos).getRoom_name());
                    intent.putExtra("room_check", m_List.get(pos).getCheck_flag());
                    intent.putExtra("room_layer",m_List.get(pos).getLayer());
                    intent.putExtra("room_start_time",m_List.get(pos).getStart_time());
                    intent.putExtra("room_end_time",m_List.get(pos).getEnd_time());
                    intent.putExtra("room_date",m_List.get(pos).getDate());
                    context.startActivity(intent);
                }
            });

            // 리스트 아이템을 길게 터치 했을 떄 이벤트 발생
            /*
            convertView.setOnLongClickListener(new OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {
                    // 터치 시 해당 아이템 이름 출력
                    Toast.makeText(context, "리스트 롱 클릭 : "+m_List.get(pos), Toast.LENGTH_SHORT).show();
                    return true;
                }
            });*/
        }

        return convertView;
    }

    // 외부에서 아이템 추가 요청 시 사용
    public void add(RoomDTO _msg) {
        m_List.add(_msg);
    }

    // 외부에서 아이템 삭제 요청 시 사용
    public void remove(int _position) {
        m_List.remove(_position);
    }
}
