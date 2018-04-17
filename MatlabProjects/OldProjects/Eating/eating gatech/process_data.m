load('raw_data');
data = [];
for i=1:20
    a = raw_data(i).acl;    
    annots = raw_data(i).annotations;
    annots = annots(annots(:,2)==1, :);
    count = size(a,1);
     
    %a = a(1:4:count, :); % downsample
    time_stamps = a(:,1);
    acl = a(:, 2:4); % accelerometer
    acl_smooth = smooth(acl, 0.9); %smoothed acceleration
    %acl_smooth_normalize = normalize(a);    
    acl_count = size(a,1);
    annot_count = size(annots, 1);    
    
    acl_annots = zeros(acl_count,2);
    t = time_stamps;
    t2 = [t(2:end); t(end)];
    for j =1:annot_count
        acl_index = find(annots(j,1)>=t & annots(j,1)<t2);
        if length(acl_index)>1
            fprintf('length>1');
            i
            acl_index
            j            
        end
        acl_annots(acl_index, 1) = annots(j,2);
        acl_annots(acl_index, 2) = annots(j,2); %both kept same for code consistency
    end
    
    acl_annot_count = sum(acl_annots(:,1)~=0);
    if acl_annot_count ~= annot_count
        fprintf('Subject %d: annot_count: %d, acl_annot_count:%d\n', i, annot_count, acl_annot_count);
        return;
    end
    
    data(i).time_stamps = time_stamps;
    data(i).acl = acl;
    data(i).acl_smooth = acl_smooth;
    data(i).acl_count = acl_count;
    
    data(i).acl_annots = acl_annots;
    data(i).annot_count = annot_count;
    data(i).pos_annot_count = sum(acl_annots(:,2));
    
end

save('data', 'data');