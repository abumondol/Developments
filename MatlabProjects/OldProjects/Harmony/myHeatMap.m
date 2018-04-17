function myHeatMap(grid, cmap)
%grid = grid';
figure;
colormap(cmap);
imagesc([0 200],[0 200], grid);
colorbar;