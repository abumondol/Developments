sub_count = length(data_usc);
for sub=1:sub_count
    fprintf('Subject %d\n', sub);
    
    acl = data_usc(sub).accel;
    annots = data_usc(sub).annots;
    annots = annots(annots(:,2)<1000, :);
        
    acl = acl(1:4:end, :);
    acl(:,2:4) = mysmooth(acl(:,2:4), 0.9);
    len = length(acl);
    annot_count = size(annots, 1);
    cur_annot_index = 1;
    cur_annot_time = annots(cur_annot_index, 1);
    features = zeros(length(1:4:len-4), 6);    
    duplicate = 0;
    
    j = 1;
    for i=1:4:len-4        
        m = mean(acl(i:i+3, 2:4));                
        t1 = acl(i, 1);
        t2 = acl(i+4, 1);        
        ind = find(annots(:,1) >=t1 & annots(:,1)<t2);
        l = length(ind);
        if l == 0
            label = 0;
        else
            label =1;
            duplicate = duplicate + l - 1;
        end
        
        features(j, :) = [t1, t2, m, label];
        j = j + 1;        
    end
    
    a_count = sum(features(:, end)==1);
    len = length(features);
    if a_count ~= annot_count-duplicate
        fprintf('Annot count error, subject %d: %d, %d\n', sub, a_count, annot_count);        
        return
    elseif j ~= len + 1
        fprintf('Feature count error, subject %d\n', sub);
        return
    end
        
    labels = zeros(len ,1);
    for i =1: len
        if features(i, end) == 1
            labels(i:i+4, 1) = ones(5,1);                
        end        
    end    
        
    filename = strcat('features/subject_', num2str(sub), '.csv');
    a = features(:, 3:5); %x, y, z
    n = sqrt(sum(a.*a, 2)); %norm
    a = a./[n, n, n];
    csvwrite(filename, [a, labels]);    
end



