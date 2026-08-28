const mongoose = require('mongoose');

const emergencySchema = new mongoose.Schema({
  studentId: { type: mongoose.Schema.Types.ObjectId, ref: 'Student' },
  type: { type: String, enum: ['medical', 'fire', 'security', 'accident', 'violence', 'natural_disaster', 'other'], required: true },
  description: { type: String },
  location: { type: String },
  room: { type: String },
  block: { type: String },
  status: { type: String, enum: ['active', 'acknowledged', 'responding', 'resolved'], default: 'active' },
  reportedAt: { type: Date, default: Date.now },
  acknowledgedBy: { type: mongoose.Schema.Types.ObjectId, ref: 'User' },
  acknowledgedAt: { type: Date },
  resolvedBy: { type: mongoose.Schema.Types.ObjectId, ref: 'User' },
  resolvedAt: { type: Date },
  respondersAssigned: [{ type: mongoose.Schema.Types.ObjectId, ref: 'User' }],
  notes: { type: String },
  severity: { type: String, enum: ['low', 'medium', 'high', 'critical'], default: 'high' }
}, { timestamps: true });

const Emergency = mongoose.model('Emergency', emergencySchema);
module.exports = Emergency;
