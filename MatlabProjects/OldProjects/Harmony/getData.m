function data = getData(window_size, step_size)
names={'Sayeed'};
if exist('harmony_raw_data.mat','file') ~=2
    read_raw_data(names);
end

if exist('harmony_raw_data_processed.mat','file') ~=2
    process_raw_data(); 
end

if exist('harmony_window_indices.mat','file') ~=2
    findWindowIndices(window_size, step_size);
end

if exist('harmony_features.mat','file')~=2
    calculateFeatures();
end

return

if exist('harmony_features.mat','file')~=2
    
    load('hwWindowIndices');
    features = calculateFeatures(data, windowIndices);
    mean_std = [mean(features); std(features)];
    features = zscore(features);
    label = (windowIndices(:,end) == 12) + 1;  % 1: hand wash, 2 = no handwash
    save('hwFeatures','features','label','mean_std');   
    %display('Calculating features done.');
end

if exist('hwGrid.mat','file')~=2
    display('Calculating grid ...');
    hw = data(data(:,16) <= 11, :);
    nhw = data(data(:,16) > 11, :);
    gridXY = gridCalculate(hw(:,[11,12]));
    gridYZ = gridCalculate(hw(:,[12,13]));    
    gridZX = gridCalculate(hw(:,[13,11]));
    
    gridXYNeg = gridCalculate(nhw(:,[11,12]));
    gridYZNeg = gridCalculate(nhw(:,[12,13]));    
    gridZXNeg = gridCalculate(nhw(:,[13,11]));    
    
    save('hwGrid','gridXY','gridYZ', 'gridZX','gridXYNeg','gridYZNeg', 'gridZXNeg');
    display('Calculating grid done.');
end

if exist('hwGridFeatures.mat','file')~=2
    display('Calculating grid features ...');        
    load('hwGrid');
    load('hwWindowIndices'); % loads windowIndices    
    m = size(windowIndices,1);    
    gridFeatures = zeros(m, 9);
    d = int16(data(:,11:13) * 10) + 100;
    for i=1:m
        s = windowIndices(i, 1);
        e = windowIndices(i, 2);
        wd = d(s:e, :);        
        gridFeatures(i,:) = calculateGridFeatures(wd, gridXY, gridYZ, gridZX);       
        %f = gridFeatures(i,:)       
    end       
    
    save('hwGridFeatures','gridFeatures');
    display('Calculating grid features done.');
end


end