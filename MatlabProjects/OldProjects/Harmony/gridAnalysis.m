function neg_weights = gridAnalysis(axes)
load('harmony_raw_data_processed'); % data_hw and data_nhw
load('harmony_window_indices'); % indices_hw and indices_nhw

temp = data_hw;
for i = 1:size(temp,1)
    t = temp{i,1};
    t = t(t(:,2)==9, axes);
    temp{i,1} =t;
end

grid = gridCalculate(temp);
myHeatMap(grid,'gray');

weights=[];

for i=1:size(data_hw,1)    
    w = gridWeights(grid, data_hw{i,1}, indices_hw{i,1}, axes); 
    weights = [weights; w];    
end

cdfPos = getCDF(weights);


weights = gridWeights(grid, data_nhw, indices_nhw, axes); 
cdfNeg = getCDF(weights);
a = [(0:100)', cdfPos, cdfNeg]

neg_weights = weights;

figure
plot(cdfPos);
hold on
plot(cdfNeg);
hold off



end