%load('data')

session_counts = [2, 5, 2, 2, 4, 5, 1]
srcFolder = 'C:/Users/mm5gg/Box Sync/Data Sets/Eating m2fed/uva_meals';

file_path = strcat(srcFolder,'/offsets.csv')
all_offsets = csvread(file_path);

sub_count = length(data)

for sub = 1:7
    sess_count = session_counts(sub);
    for sess=1:sess_count
        file_path = strcat(srcFolder,'/subject_', num2str(sub), '/subject', num2str(sub), '_right_session', num2str(sess),'.wada')
        raw_data = csvread(file_path);
        
        file_path = strcat(srcFolder,'/annotations/processed/subject', num2str(sub), '_annotations_right_session', num2str(sess),'.csv')
        annots = csvread(file_path);
        
        offset = all_offsets(sub, sess);
        
        res = process_data(raw_data, offset, annots, []);
        sub_count = sub_count + 1;
        data(sub_count).subject_id = strcat(num2str(sub), '_',  num2str(sess));
        data(sub_count).accel = res.accel;
        data(sub_count).grav = res.grav;
        data(sub_count).annots = res.annots;
        %data(sub_count).accel_annot_indices = res.accel_annot_indices;
        data(sub_count).annots_adjusted = res.annots_adjusted ;
        data(sub_count).eat_annot_count = res.eat_annot_count;
        data(sub_count).drink_annot_count = res.drink_annot_count;
        data(sub_count).non_bite_annot_count = res.non_bite_annot_count;        
    end    
     
end

save('data', 'data');
