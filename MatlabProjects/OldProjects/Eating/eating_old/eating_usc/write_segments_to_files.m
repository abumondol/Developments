load('data_usc');
data = data_usc;
load('segments_usc');
sub_count = length(data);

file_no = 1;
ground_truth_list = [];
for sub = 1:sub_count
    d = data(sub).accel;
    annots = data(sub).accel_annot_indices;    
    windows = segments_usc(sub).windows;    
    window_count = size(windows, 1);
    
    for i = 1: window_count
        fprintf('%d, %d, %d\n', sub, i, file_no);
        a = windows(i,1);
        b = windows(i,2);
        c = find(annots(:,1) >= a & annots(:,1) <= b);
        if length(c) > 0
            ground_truth_list = [ground_truth_list; sub, file_no, 1];
        else
            ground_truth_list = [ground_truth_list; sub, file_no, 0];
        end
        file_name = strcat('files/',num2str(file_no),'.csv');
        dlmwrite(file_name, d(a:b, 2:4));
        file_no = file_no + 1;
    end
end

dlmwrite('ground_truth_list.csv',ground_truth_list);
