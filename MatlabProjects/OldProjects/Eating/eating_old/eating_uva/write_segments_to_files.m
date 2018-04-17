load('data');
load('segments');
sub_count = length(data);

ground_truth_list = csvread('../eating_usc/ground_truth_list.csv');
file_no = size(ground_truth_list, 1) + 1
last_sub = max(ground_truth_list(:,1))

for sub = 1:sub_count
    sess_count = length(data(sub).session);
    for sess = 1:sess_count
        d = data(sub).session(sess).accel;
        annots = data(sub).session(sess).accel_annot_indices;    
        windows = segments(sub).session(sess).windows;    
        window_count = size(windows, 1);

        for i = 1: window_count
            fprintf('%d, %d, %d, %d\n', sub, sess, i, file_no);
            a = windows(i,1);
            b = windows(i,2);
            c = find(annots(:,1) >= a & annots(:,1) <= b);
            if length(c) > 0
                ground_truth_list = [ground_truth_list; sub+last_sub, file_no, 1];
            else
                ground_truth_list = [ground_truth_list; sub+last_sub, file_no, 0];
            end
            file_name = strcat('../eating_usc/files/',num2str(file_no),'.csv');
            dlmwrite(file_name, d(a:b, 2:4));
            file_no = file_no + 1;
        end
    end
end

dlmwrite('ground_truth_list.csv',ground_truth_list);
