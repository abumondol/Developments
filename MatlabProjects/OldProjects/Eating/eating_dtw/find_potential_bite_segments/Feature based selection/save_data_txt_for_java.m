sub_count = length(data);
offset = 16;
res = zeros(sub_count, 4);
dest_folder = 'C:/ASM/developments/JavaProjects/Eating Analysis DTW/mydata/';


for s = 1:sub_count
    fprintf('Subject %d\n', s);
        
    segs = segments_for_pat(s).segspos;    
    if ~isempty(segs)
        mp = [segs(:,1)-1, segs(:, 3)]; % -1 in minpoints because index starts from zero in java, in contrast to 1 in matlab       
        filename = strcat(dest_folder, 'segment_info/segspos_', num2str(s-1));    
        dlmwrite(filename, mp, 'precision',10);
        
        annots = data(s).annots;
        annots = annots(annots(:, 2)<1000, :);
        filename=strcat(dest_folder, 'annot_data/annots_', num2str(s-1));    
        dlmwrite(filename, annots, 'precision',10);
    end
    
    segs = segments_for_pat(s).segsneg;    
    mp = [segs(:,1)-1, segs(:, 3)]; % -1 in minpoints because index starts from zero in java, in contrast to 1 in matlab       
    filename = strcat(dest_folder, 'segment_info/segsneg_', num2str(s-1));    
    dlmwrite(filename, mp, 'precision',10);
    
    %accel = data(s).accel(:, 2:4);
    %filename=strcat(dest_folder, 'accel_data/accel_', num2str(s-1));    
    %dlmwrite(filename, accel, 'precision',10);
    
    
    
end