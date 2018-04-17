function process_raw_data()
load('harmony_raw_data'); % loads raw_data_hw and raw_data_nhw

data_hw = {};
data_nhw = [];
   
for i=1:length(raw_data_hw)
    d = raw_data_hw{i,1};
    %size(d)    
    cond = d(:,2) == 1 | d(:,2) == 4 | d(:,2) == 9 | d(:,2) == 10;
    d = d(cond, [1 2 4 5 6]);  
    d(:,1) = d(:,1)/1000000;
    
    for j= 1:size(d,1)
        if d(j,2) == 9
            break;
        end
    end
    d = d(j:end, :);    
    data_hw{i,1} = d;    
end

d = raw_data_nhw;
cond = d(:,2) == 1 | d(:,2) == 4 | d(:,2) == 9 | d(:,2) == 10;
d = d(cond, [1 2 4 5 6]);  
d(:,1) = d(:,1)/1000000;
    
for j= 1:size(d,1)
    if d(j,2) == 9
        break;
    end
end
display(j);
d = d(j:end, :);    
data_nhw = d;

save('harmony_raw_data_processed','data_hw', 'data_nhw');

end
